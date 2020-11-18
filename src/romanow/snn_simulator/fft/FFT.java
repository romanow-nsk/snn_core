package romanow.snn_simulator.fft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import romanow.snn_simulator.gammatone.GTFilterBank;
import romanow.snn_simulator.gammatone.GTFilterOriginal;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import romanow.snn_simulator.gammatone.GPUFiltersKernel;
import romanow.snn_simulator.gammatone.GPUGTFilterBank;
 
public class FFT implements FFTBinStream{
    //------------- СТАТИЧЕСКАЯ ЧАСТЬ
    private static float expValues[]=null;         // Подчитаниие заранее значения экспоненты
    private static float dExp=0.01F;                // Шаг экспоненты
    private static float expLimit=50;              // Диапазон экспоненты
    private static void calcExp(){
        if (expValues!=null)
            return;
        expValues = new float[(int)(expLimit/dExp)];
        for(int i=0;i<expValues.length;i++)
            expValues[i] = (float)(Math.exp(-i*dExp));
        }
    public static float getExp(float x){       // Значение 
        if (x<0 || x>expLimit)
            return (float)(Math.exp(-x));
        calcExp();
        return expValues[(int)(x/dExp)];
        }
    //---------------------------------------------------------------------------
    //private int W=1024;                            // Кол-во точек в преобразовании
    //private int GPUmode=0;
    //private boolean FFTWindowReduce=false;
    //private int procOver=0;                        // Процент перекрытия окна за шаг
    //private boolean logFreqMode=false;
    //private boolean p_Cohleogram=false;
    //private int subToneCount=4;                  // Дискретность - 1/8 тона (1/4 полутона)
    public final static int formatVersion=1;       // Версия формата
    public final static String formatSignature="CASA Spectrums Binary Data";
    public final static int sizeHZ = 44100;        // Частотный диапазон оцифровки
    public final static float Ccontr = 33;         // Частота до контр-октавы
    public final static int Octaves = 10;          // Кол-во октав
    public final static int  Size0= 1024;          // 1024 базовая степень FFT
    private int currentFormatVersion=0;
    private float stepHZLinear;                    // Дискретность частоты
    private float totalMS=0;                       // Текущий момент времени    
    private float stepMS;                          // Шаг оцифровки спектра
    private float countGTF;                        // Шагов гамматон-фильтра
    private float compressGrade=0;
    private boolean compressMode=false;
    private float kAmpl=1;
    private GPU gpu=null;
    private FFTParams pars = null;
    //------------ текущие спектры и волны -------------------------------------
    private float wave[]=null;                     // Текущая волна
    private FFTArray spectrum=null;                // Текущий спектр
    private FFTArray logSpectrum=null;             // Спектр октавный (логарифмический диапазон)
    private FFTArray gammaTones=null;              // Кохлеаграмма =ПОКА 1 значение
    private FFTArray GTSpectrum=null;
    private boolean preloadMode=false;              // Режим предварительной загрузки
    private int nblock=0;                           // Индекс спектра (шаг модели)
    private FFTArray GTSpectrumList[]=null;         // preload кохлеограмма
    private FFTArray logSpectrumList[]=null;        // preload спектр
    private float fullWave[]=null;                  // preload волна
    private Complex[] complexSpectrum=null;
    //------------ тоновая шкала -----------------------------------------------
    private int toneIndexes[]=null;                 // Индексы тонов в линейном спектре
    private GTFilterBank filterBank=null;
    //--------------------------------------------------------------------------
    private FFTAudioSource audioInputStream=null;
    private FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    private final static String noteList[]={"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    
    public static int pow2(int v){
        return 1<<v;
        }
    //----------------- Определить граничную ноту шагу частоты
    public String getFirstValidNote(){
        int lastEqIdx=0;
        for(int i=1;i<toneIndexes.length;i++){
            if (toneIndexes[i]==toneIndexes[i-1])
                lastEqIdx=i;
            }
        return getNoteNameByIndex(lastEqIdx/pars.subToneCount());
        }
    public static String getShortNoteNameByIndex(int idx){
        String out=noteList[idx%12];
        int i1=idx/12;
        switch(i1){
            case 0: out = "к."+out; break;
            case 1: out = "б."+out; break;
            case 2: out = "м."+out; break;
            default: out = ""+(i1-2)+" "+out; break;
            }
        return out;
        }
    public static String getNoteNameByIndex(int idx){
        String out=noteList[idx%12]+" ";
        int i1=idx/12;
        switch(i1){
            case 0: out+="контр."; break;
            case 1: out+="большая"; break;
            case 2: out+="малая"; break;
            default: out+=""+(i1-2)+" октава"; break;
            }
        return out;
        }
    public void clearPreload(){
        logSpectrumList = null;
        GTSpectrumList = null;
        }
    public boolean isPreload(){
        return logSpectrumList!=null && (!pars.p_Cohleogram() || GTSpectrumList!=null);
        }
    public int getGTFilterSize(){
        return filterBank.size();
        }
    public FFTAudioSource getAudioInputStream() {
        return audioInputStream;
        }
    public void setCompressGrade(float compressGrade) {
        this.compressGrade = compressGrade;
        }
    public void setCompressMode(boolean compressMode) {
        this.compressMode = compressMode;
        }
    public void clearMaxAmpl() {
        spectrum.clearMax();
        }
    public void setKAmpl(float vv){
        kAmpl = vv;
        }
    public float getMaxAmpl() {
        return spectrum.getMax();
        }
    public float getTotalMS() {
        return totalMS;
        }
    public float getStepMS() {
        return stepMS;
        }
    public int getSubToneCount() {
        return pars.subToneCount();
        }
    public void close(){
        close(null);
        }
    public float[] getWave() {
        return wave;
        }
    public float[] getSpectrum() {
        return spectrum.getOriginal();
        }
    public float[] getLogSpectrum() {
        return logSpectrum.getOriginal();
        }
    public int[] getToneIndexes() {
        return toneIndexes;
        }
    public float getStepHZLinear() {
        return stepHZLinear;
        }
    public boolean isLogFreqMode() {
        return pars.logFreqMode();
        }
    public void setLogFreqMode(boolean mode) {
        pars.setLogFreqMode(mode);
        }
    public FFTParams getParams(){
        return pars;
        }
    public void close(FFTCallBack back){
        try {
            if (audioInputStream!=null)
                audioInputStream.close();
            audioInputStream = null;
            } catch (IOException ex) {}
        if (back!=null)
            back.onFinish();
        }
    // Фильтровать октавный спектр (для отображения) - ОКТАВНЫЙ, КОМПРЕССИРОВАННЫЙ
    public float[] getFilteredSpectrum(float toneFilter[],FFTSpectorFilter filter){
        FFTArray out = new FFTArray(toneIndexes.length);
     	float radice = (float)(1 / Math.sqrt(wave.length));
        for(int i=0;i<toneIndexes.length;i++){
            if (filter!=null){
                Complex xx = new Complex(logSpectrum.get(i),0);
                out.set(i,(float)filter.convert(xx,toneFilter[i]).abs());
                }
            else
                out.set(i,toneFilter[i]);       // Сам нейронный слой = спектр
            }
        return out.getOriginal();
        }
    public void setFFTParams(FFTParams pars){
        this.pars = pars;
        calcFFTParams();
        gpu = new GPU(pars.p_GPU());
        gpu.printGPUInfo();
        }
    public String fullGPUInfo(){
        return new GPU(true).fullGPUInfo();
        }
    /*
    public void setFFTParams(int W, int procOver, boolean logFreqMode, 
        int subToneCount, boolean p_Cohleogram, boolean p_GPU, boolean FFTWindowReduce,int GPUmode){
        this.GPUmode = GPUmode;
        this.FFTWindowReduce = FFTWindowReduce;
        this.W = W;
        this.procOver = procOver;
        calcFFTParams();
        this.logFreqMode= logFreqMode;
        this.subToneCount = subToneCount;
        this.p_Cohleogram = p_Cohleogram;
        gpu = new GPU(p_GPU);
        gpu.printGPUInfo();
        }
    */
    public int getLogSpectrumSize(){
        return Octaves*12*pars.subToneCount();
        }
    public void calcFFTParams(){
        stepHZLinear = ((float)sizeHZ)/pars.W();
        stepMS = 10*pars.W()*(100-pars.procOver())/sizeHZ;
        countGTF =  pars.W()*(100-pars.procOver())/100;
        totalMS=0;
        filterBank = new GTFilterBank();
        createSubToneIndexes();
        calcExp();
        }
    private void createSubToneIndexes(){
        float c0 = Ccontr;
        int octSize = 12*pars.subToneCount();
        int out[]=new int[Octaves*octSize];
        int k=0;
        for(int i=0;i<10;i++,c0*=2){
            for(int j=0;j<octSize;j++,k++){
                float tone = (float)(c0*Math.pow(2, ((float)j)/octSize));
                int idx0 = (int)(tone/stepHZLinear);
                out[k] = idx0;
                }
            }
        toneIndexes = out;
        }
    public int getAudioLength(FFTAudioSource audioInputStream, FFTCallBack back){
        calcFFTParams();
        createFilterBank();
        filterBank.clear();
        if (audioInputStream==null){
            back.onError("Не выбран источник аудио");
            return -1;
            }
        this.audioInputStream = audioInputStream;
        back.onMessage("Длина "+audioInputStream.getFrameLength()+
            " дискретность "+stepHZLinear+" гц начальная нота = "+getFirstValidNote());
        int size = (int)audioInputStream.getFrameLength();
        return size;
    }
    public  boolean preloadWave(FFTAudioSource audioInputStream, FFTCallBack back){
        int size = getAudioLength(audioInputStream, back);
        if (size == -1)
            return false;
        fullWave = new float[size];
        try {
            audioInputStream.read(fullWave, 0, size);
            } catch(IOException ee){
                back.onError("Ошибка чтения аудио");
                clearPreload();
                return false;
                }
        return true;
    }
    //----------------- Прямая конвертация гамматона в массив ------------------
    public void preloadFullCohleogramm(FFTAudioSource audioInputStream, FFTCallBack back){
        if (!preloadWave(audioInputStream,back))
            return;
        int nQuant = pars.W()*(100-pars.procOver())/100;
        int sz = fullWave.length/nQuant;
        int dd=sz/20;
        if (fullWave.length%nQuant !=0 ) sz++;
        GTSpectrumList  = new FFTArray[sz];
        TimeCounter tc = new TimeCounter("Конвертация кохлеограммы");
        back.onMessage("Отсчетов кохлеограммы:"+sz);
        if (gpu.devicePresent()){
            GPUFiltersKernel gpuFilter = new GPUFiltersKernel();
            GTSpectrumList = gpuFilter.execute(filterBank.getFilterBank(),fullWave,nQuant,sz,pars.GPUmode());
            for(int i=0;i<sz;i++)
                GTSpectrumList[i].compress(compressMode, compressGrade,kAmpl);
            }
        else{
            wave = new float[pars.W()];
            for(int i=0, base=0; i<sz; i++, base+=nQuant){
                for(int j=0;j<pars.W();j++){
                    wave[j] = base+j<fullWave.length ? fullWave[base+j] : 0;
                    }
                filterBank.procCohleogramm(wave, pars.procOver(),gpu);
                GTSpectrum  = filterBank.getGTSpectrum();
                GTSpectrum.compress(compressMode, compressGrade,kAmpl);
                GTSpectrumList[i] = GTSpectrum;
                if (i%dd==0)
                    back.onMessage(""+i*100/sz+"%");
                }
            }
        tc.addCount(0);
        back.onMessage(tc.toString());
        }
    //----------------- Прямая конвертация спектра в массив ------------------
    public void preloadFullSpectrum(FFTAudioSource audioInputStream, FFTCallBack back){
        if (!preloadWave(audioInputStream,back))
            return;
        int nQuant = pars.W()*(100-pars.procOver())/100;
        int sz = fullWave.length/nQuant;
        if (fullWave.length%nQuant !=0 ) sz++;
        logSpectrumList = new FFTArray[sz];
        wave = new float[pars.W()];
        back.onMessage("Отсчетов спектра:"+sz);
        TimeCounter tc = new TimeCounter("Конвертация спектра");
        int dd = sz/20;
        for(int i=0, base=0; i<logSpectrumList.length; i++, base+=nQuant){
            spectrum = new FFTArray(wave.length);
            logSpectrum=new FFTArray(toneIndexes.length);
            for(int j=0;j<pars.W();j++){
                wave[j] = base+j<fullWave.length ? fullWave[base+j] : 0;
                }
            fftDirectStandart(pars.FFTWindowReduce()); // Переменный (фикс.) размер окна
            spectrum.compress(compressMode,compressGrade,kAmpl);
            convertToLog(true);                 // Линейный/триангулярный
            //spectrum.nextStep();                // ?????????
            logSpectrumList[i] = logSpectrum;
            if (i%dd==0)
                back.onMessage(""+i*100/sz+"%");
            }
        tc.addCount(0);
        back.onMessage(tc.toString());
        }
    //--------------------------------------------------------------------------
    private TimeCounter tc = 
        new TimeCounter(new String[]{"Чтение","БПФ","Компрессия","log-шкала","Гамматон","Модель","Визуализация","Общее"});
    public void addCount(int idx){
        tc.addCount(idx);
        }
    // kOver - коэфф. перекрытия скользящего буфера при последующем чтении
    public void fftDirect(FFTAudioSource audioInputStream, FFTCallBack back){
        if (!preloadWave(audioInputStream,back))
            return;
        preloadMode=false;
        tc.clear();
        back.onStart(stepMS);
        wave = new float[pars.W()];
        spectrum = new FFTArray(wave.length);
        logSpectrum=new FFTArray(toneIndexes.length);
        nblock=0;
        int size = fullWave.length;
        int nQuant = pars.W()*(100-pars.procOver())/100;
        try {
            int read=0;
            int i=0;
            int base=0;
            int lnt=0;
            tc.clear();
            do  {
                tc.fixTime();
                for(int j=0;j<pars.W();j++){
                    wave[j] = j<size ? fullWave[base+j] : 0;
                    }
                base += nQuant;
                size -= nQuant;
                tc.addCount(0);
                fftDirectStandart(pars.FFTWindowReduce()); // Переменный (фикс.) размер окна
                tc.addCount(1);
                spectrum.compress(compressMode,compressGrade,kAmpl);
                tc.addCount(2);
                convertToLog(true);                 // Линейный/триангулярный
                tc.addCount(3);
                if (pars.p_Cohleogram()){
                    filterBank.procCohleogramm(wave, pars.procOver(),gpu);
                    GTSpectrum  = filterBank.getGTSpectrum();
                    tc.addCount(4);
                    GTSpectrum.compress(compressMode, compressGrade,kAmpl);
                    tc.addCount(2);
                    }
                tc.addCount(4);
                //spectrum.nextStep();
                //GTSpectrum.nextStep();
                boolean bb = back.onStep(nblock, tc.getTotal(), totalMS, this);
                nblock++;
                if (!bb)
                    break;
                tc.addCountTotal(7);
                totalMS += stepMS;
                } while(size>0);
            back.onMessage("Блоков "+nblock);
            back.onMessage(tc.toString());
            close(back);
            } catch (Exception e) { 
                back.onError(e.toString());
                close(back);
                }
        }
    public void fftDirect(FFTCallBack back){
        if (!isPreload()){
            back.onMessage("Нет предварительных данных");
            return;
            }
        preloadMode = true;
        tc.clear();
        back.onStart(stepMS);
        int nblocks=0;
        try {
            for(nblocks=0; nblocks<logSpectrumList.length;nblocks++){
                tc.fixTime();
                tc.addCount(0);
                tc.addCount(1);
                tc.addCount(2);
                tc.addCount(3);
                tc.addCount(4);
                logSpectrum = logSpectrumList[nblocks];
                if (pars.p_Cohleogram())
                    GTSpectrum = GTSpectrumList[nblocks];
                //spectrum.nextStep();
                //GTSpectrum.nextStep();
                boolean bb = back.onStep(nblocks+1, tc.getTotal(), totalMS, this);
                if (!bb)
                    break;
                tc.addCountTotal(7);
                totalMS += stepMS;
                }
            back.onMessage("Блоков "+nblocks);
            back.onMessage(tc.toString());
            close(back);
            } catch (Exception e) { 
                back.onError(e.toString());
                close(back);
                }
        }
    //--------- Простое усреднение по диапазону
    private void convertToLog(boolean triangle){
        if (triangle) 
            convertToLogTriangle();
        else
            convertToLogLinear();
        }
    private void convertToLogLinear(){
        for(int i=0;i<toneIndexes.length;i++){
            int idx0 = (i==0 ? toneIndexes[0]-1 : (toneIndexes[i]+toneIndexes[i-1])/2);
            int idx1 = (i==toneIndexes.length-1 ? 
                toneIndexes[i]+10 
                : (toneIndexes[i]+toneIndexes[i+1])/2);
            float vv=0;
            for(int j=idx0;j<idx1;j++)
               vv += spectrum.get(j);
            vv /= (idx1-idx0);
            logSpectrum.set(i,vv);
            }
        }
    //---------- Конвертировать по треугольному фильтру ---------------------
    private void convertToLogTriangle(){
        for(int i=0;i<toneIndexes.length;i++){
            /*  Перекрывающиеся диапазоны
            int idx0 = (i==0 ? toneIndexes[0]-1 : toneIndexes[i-1]);
            int idx1 = (i==toneIndexes.length-1 ? 
                toneIndexes[i]+10 
                : toneIndexes[i+1]);
            */
            // Неперекрывающиеся диапазоны
            int idx0 = (i==0 ? toneIndexes[0]-1 : (toneIndexes[i]+toneIndexes[i-1])/2);
            int idx1 = (i==toneIndexes.length-1 ? 
                toneIndexes[i]+10 
                : (toneIndexes[i]+toneIndexes[i+1])/2);
            if (idx0+3>=idx1){          // Если диапазон УЗКИЙ
                logSpectrum.set(i,spectrum.get(toneIndexes[i]));
                continue;
                }
            float vv=0;
            int delta = (idx1-idx0+1)/2;
            for(int j=idx0;j<=idx1;j++){
                vv += spectrum.get(j)*(delta-(Math.abs(toneIndexes[i]-j)))/delta;
                }
            //System.out.println(""+idx0+" "+idx1+" "+delta);
            vv = vv*2/(idx1-idx0);
            logSpectrum.set(i,vv);
            }
        }
    // Фильтровать и преобразовать в волну
    public float[] convertToWave(float toneFilter[],FFTSpectorFilter filter){
        Complex xx[] = (Complex [])complexSpectrum.clone();
        if (filter==null){
            for(int i=0;i<xx.length;i++)
                xx[i]= new Complex(0,0);
            }
        for(int i=0;i<toneIndexes.length;i++){
            int idx0 = (i==0 ? toneIndexes[0]-1 : (toneIndexes[i]+toneIndexes[i-1])/2);
            int idx1 = (i==toneIndexes.length-1 ? 
                toneIndexes[i]+10 
                : (toneIndexes[i]+toneIndexes[i+1])/2);
            for(int j=idx0;j<idx1;j++){
                if (filter!=null){
                    xx[j] = filter.convert(xx[j],toneFilter[i]);    // Симметричную часть спектра фильтровать
                    xx[xx.length-1-j] = filter.convert(xx[xx.length-1-j],toneFilter[i]);
                    }
                else{       
                    // Сам слой - модулятор считать спектром
                    // Спектр восстанавливается треугольником относительно центральной частоты
                        xx[j]=new Complex(toneFilter[i]).multiply(20*(idx1-idx0)/(1+Math.abs(j-toneIndexes[i])));
                    }
                }
            }
        return backToWave(false,xx);
        }
    //=============================== ГАММАТОН =================================
    // При уменьшении частоты кратно - кол-во фильтров уменьшается по октавам линейно
    // Частота вызова на каждой октаве уменьшается (таблица коэффициентов)
    public void createFilterBank(){
        filterBank = new GTFilterBank();
        filterBank.createGTFilterBank(pars.subToneCount());
        GTSpectrum = new FFTArray(filterBank.size());
        }
    public float []getGammatoneStatic(int note){       // Это ИСХОДНИК !!!!!!!
        float c0 = Ccontr;
        while(note >= 12){
            c0 *=2;
            note -=12;
            }
        float tone = (float)(c0*Math.pow(2, note/12.));
        double out[] = new double[wave.length];
        GTFilterOriginal.filter(convert(wave), sizeHZ, (int)tone, false, out, null, null,null);
        return FFT.convert(out);
        }
    public float []getGammatone(int note,boolean env){ // Для одного тона
        return  filterBank.getGammatone(wave, pars.procOver(), note, env);
        }

    private float F_SCALE = 3f;
    public float []getMultipleSpectrum(boolean multiple){
        float out[] = logSpectrum.getOriginal();
        float vv[] = GTSpectrum.getOriginal();
        for(int i=0;i<out.length;i++){
            if (multiple)
                out[i] *= vv[i];
            else
                out[i] = (float)Math.sqrt(F_SCALE*F_SCALE*out[i]*out[i] + vv[i]*vv[i]);
            }
        FFTArray.normalizeLocal(out);
        return out;
        } 

    public float []getGTSpectrum(){ 
        return GTSpectrum.getOriginal();
        }
    public void gammatoneFilter(){
        long timeStart = new Date().getTime();
        gammaTones = new FFTArray(toneIndexes.length);
        float tmp[] = new float[wave.length];
        float c0 = Ccontr;
        int octSize = 12*pars.subToneCount();
        int out[]=new int[Octaves*octSize];
        int k=0;
        for(int i=0;i<10;i++,c0*=2){
            for(int j=0;j<octSize;j++,k++){
                float tone =(float)(c0*Math.pow(2, ((float)j)/octSize));
                GTFilterOriginal.filter(convert(wave), sizeHZ, (int)tone, false, convert(tmp), null, null,null);
                float sum=0;
                for(int kk=0;kk<tmp.length;kk++)
                    sum += tmp[kk];
                sum/=tmp.length;
                gammaTones.set(k,sum);
                }
            }
        }
    //--------------------------------------------------------------------------
    /**
    * Выполняет БПФ массива значений с помощью Apache Commons Math
    *
    * inputData - массив входных значений
    * массив с результатами БПФ
    */
    private float[] reduceTo(float in[]){
        float out[] = new float[in.length/2];
        for(int i=0;i<out.length;i++)
            out[i]=in[i];
        return out;
        }
    public void fftDirectStandart(boolean mode){
        if (mode)
            fftDirectStandartIterative();
        else
            fftDirectStandart();
        }
    public void fftDirectStandartIterative(){
        fftDirectStandart();
        float tmp[] = wave;                 // Укорочение интервалов для высоких частот
        int pow = 1;                        // ПО ОКТАВАМ
        int oct=2;                          // Частота, с которой копируется спектр
        int base=0;
        int nextBase = toneIndexes[pars.subToneCount()*12*oct];
       	float radice = (float)(1 / Math.sqrt(tmp.length));
        while(tmp.length>=2018){            // 
            Complex xx[] = fft.transform(convert(tmp),TransformType.FORWARD);
            if (pow==1)
                complexSpectrum = xx;
         	//float radice = (float)(1 / Math.sqrt(tmp.length));
            for(int i = base; i < tmp.length/2 + 1 && i<nextBase; i++){
                for(int j=0;j<pow;j++)
                    spectrum.set(i*pow+j, (float)(xx[i].abs()*radice));
                }
            tmp = reduceTo(tmp);
            pow*=2;
            base = nextBase;
            nextBase*=2;
            }
        }
    public void fftDirectStandart(){
        long timeStart = new Date().getTime();
        complexSpectrum = fft.transform(convert(wave),TransformType.FORWARD);
     	float radice = (float)(1 / Math.sqrt(wave.length));
        for(int i = 0; i < wave.length/2 + 1; i++){
            spectrum.set(i, (float)complexSpectrum[i].abs()*radice);
            }
        }
    //--------------------------------------------------------------------------
    public float []backToWave(boolean test, Complex in[]){
        Complex backWave[] = fft.transform(in,TransformType.INVERSE);
        float vv[] = new float[wave.length];
        for(int i=0;i<wave.length;i++){
            vv[i] = (float)backWave[i].abs();
            if (test && !(Math.abs(wave[i]-vv[i])<0.01 || Math.abs(wave[i]+vv[i])<0.01 ))
                System.out.println(""+i+" "+wave[i]+" "+vv[i]);
            }
        return vv;
        }
    //-------------------------------------------------------------------------
    public static double []convert(float in[]){
        double out[]=new double[in.length];
        for(int i=0;i<in.length;i++)
            out[i]=in[i];
        return out;
        }
    public static float []convert(double in[]){
        float out[]=new float[in.length];
        for(int i=0;i<in.length;i++)
            out[i]=(float)in[i];
        return out;
        }
    //-----------------------------------------------------------------------
    public static void main(String argv[]){
        FFT fft = new FFT();
        FFTAudioFile file = new FFTAudioFile();
        FFTCallBack back = new FFTCallBack(){
            @Override
            public void onStart(float msOnStep) {
                System.out.println("Стартанул");
                }
            @Override
            public void onFinish() {
                System.out.println("Закончил");
                }
            @Override
            public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft) {
                //float vv[]=fft.getWave();
                //for(int i=0;i<vv.length;i++)
                //    System.out.print(" "+vv[i]);
                //System.out.println();
                //vv=fft.getSpectrum();
                //for(int i=0;i<vv.length;i++)
                //    System.out.print(" "+vv[i]);
                //System.out.println();
                float vv[]=fft.getLogSpectrum();
                System.out.println("Блок " + nBlock+" Спектр= "+vv.length+" Время (мс)="+totalMS);
                for(int i=0;i<vv.length;i++)
                    System.out.print(" "+vv[i]);
                System.out.println();
                return true;
                }
            @Override
            public void onError(String mes) {
                System.out.println(mes);
                }
            @Override
            public void onMessage(String mes) {
                System.out.println(mes);
                }
            };
        file.testAndOpenFile(FFTAudioFile.Open,"../Waves/BluesMono.wav", 44100, back);
        fft.setFFTParams(new FFTParams(1024, 0, false,2,false,false,false,0));
        fft.fftDirect(file,back); 
        }

    @Override
    public void load(DataInputStream in, int formatVersion) throws IOException {
        try {
            String sign = in.readUTF();
            if (!sign.equals(formatSignature))
                throw new IOException("Формат файла - несовпадение сигнатуры");
            } catch (Exception ex){
                throw new IOException("Формат файла - ошибка чтения сигнатуры");
                }
            currentFormatVersion = in.readInt();
        pars = new FFTParams();
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(formatSignature);
        out.writeInt(formatVersion);
        pars.save(out);
    }
}