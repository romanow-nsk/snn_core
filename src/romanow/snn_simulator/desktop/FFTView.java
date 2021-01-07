/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import com.sun.scenario.Settings;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.layer.*;
import romanow.snn_simulator.neuron.N_BaseNeuron;
import romanow.snn_simulator.GBL;
import java.awt.FileDialog;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTAudioFile;
import romanow.snn_simulator.fft.FFTAudioSource;
import romanow.snn_simulator.fft.FFTCallBack;
import romanow.snn_simulator.fft.FFTSpectorFilter;
import romanow.snn_simulator.fft.OutputDataPlayer;
import org.apache.commons.math3.complex.Complex;
import romanow.snn_simulator.I_LayerModel;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.fft.AudioSourceFactory;
import romanow.snn_simulator.lang.ModelCreator;
import romanow.snn_simulator.lang.NLM_Constructive;
import romanow.snn_simulator.model.ModelFactory;
import romanow.snn_simulator.model.NLM_Proxy;
import romanow.snn_simulator.neuron.NeuronFactory;
import romanow.snn_simulator.fft.FFTAudioTextFile;
import romanow.snn_simulator.fft.FFTFileSource;
import romanow.snn_simulator.fft.FFTParams;

public class FFTView extends javax.swing.JFrame implements LayerWindowCallBack{
    private String  settingsFileName="CASASettings.dat";
    private boolean p_RealTime=true;        // Моделировать в реальном времени
    private boolean p_Play=true;            // Воспроизведение исходного
    private boolean p_PlayFilter=true;      // Воспроизведение фильтра
    private boolean p_ShowAmplSpector=true; // Окно амплитудного спектра
    private String  p_lastFileName="";
    private String  p_lastFileDir="";
    private int     p_OverProc=75;          // Процент перекрытия окна
    private int     p_BlockSize=16;         // Размер блока в 1024
    private int     p_SampleType=0;         // Тип источника
    private boolean p_Compress=true;
    private int     p_CompressStage=26;
    private int     p_SubToneCount=4;
    private int     p_GPUmode=0;    
    private boolean p_LogFreq=true;         // Логарифм. шкала частот
    private boolean p_Repeat=false;         // Режим повторения
    private String  p_ModelId="";
    private String  p_NeuronId="";
    private String  p_LayerId="";
    private float  p_Ampl=1;               // Глубина фильтрации
    private boolean p_Filtered=false;       // Окно отфильтрованного спектра
    private int     p_FilterMode=0;         // Вид фильтра
    private boolean p_GTFEnabled=false;     // Разрешение гамматона
    private int     p_GTFNote=0;            // Нота для гамматона
    private boolean p_Cohleogram=false;
    private int     p_ModelSourceType=0;    // 0-спектр, 1-кохлеограмма
    private boolean p_SrcSpectrum=false;
    private boolean p_LearningMode=false;   // Режим обучения
    private boolean p_GraphFX=false;        // Статистика в виед графика FX
    private boolean p_GPU=false;
    private boolean p_MultipleSK=false;     // Произведение спектр*гамматон (false - сумма квадратов)
    private boolean p_White=false;          // Белый фон графиков
    private boolean p_OutAmpl=false;        // Выходная амплиуда - окно
    private int p_LogLevel=0;               // Уровень лога...
    private int p_StatId=0;                 // Номер статистики
    private boolean p_FFTWindowReduce=false;// Сокращение размера окна по октавам
    private int     kSmooth=50;             // Циклов сглаживания
    private boolean statSmooth=true;        // Режим сглаживания
    private boolean statMiddle=true;        // Вывод статистики среднего
    private boolean statDisp=false;         // Вывод статистики дисперсии
    private boolean statDiffT=false;
    private boolean statDiffF=false;
    private int nFirstMax=10;               // Количество максимумов в статистике (вывод)
    private boolean removeExp=false;        // Фильтр ВЧ (удаление тренда)
    private int nFirstExpPoints=10;         // Количестов точек, используемых в удалении
    private int noFirstPoints=10;           // Отрезать точек справа и слева
    private int noLastPoints=10;
    private double kMultiple=3.0;
    private int nTrendPoints=0;             // Точек при сглаживании тренда
    //--------------------------------------------------------------------------
    private int nGTFStage=500;              // Усреднение кохлеограммы
    private final int KF100 = FFT.sizeHZ/100;
    private OutputDataPlayer player = new OutputDataPlayer(10);
    volatile private LayerWindow panels[]={null,null,null,null,null,null,null,null,null};
    //0 - Окно спектра - амплитудное
    //1 - Окно спектра - временное 
    //2 - Окно выходов НС
    //3 - Окно отфильтрованного спектра
    //4 - Окно гамматона
    //5 - Окно кохлеограммы
    //6 - окно спектр*огибающая кохлеограммы
    //7 - статистика
    private NL_DigitalSource inputs=null;
    private I_LayerModel model=null;
    volatile boolean isRun=false;
    volatile boolean isPause=false;
    private FFT fft = new FFT();
    private long lastClock=0;
    private boolean repeat=false;           // Запуск потока первый или повторый 
    private float kAmpl = 1;                // Коэффициент усиления        
    private BoxFactory<N_BaseNeuron> neuronFactory;
    private BoxFactory<NL_Integrate> layerFactory;
    private BoxFactory<I_LayerModel> modelFactory;
    private BoxFactory<LayerStatistic> statFactory;
    private BoxFactory<FFTAudioSource> sourceFactory;
    private ModelFactory factory;
    private LayerStatistic selectedStat = null;
    private LayerStatistic inputStat = new LayerStatistic("Входные данные");
    I_NetParams paramsAdapter = new I_NetParams(){
        @Override
        public int getSynapsesCount() {
            return  Integer.parseInt(NSynapses.getText());            
            }
        @Override
        public int getNeightborsCount() {
            return  Integer.parseInt(NNeightbors.getText());            
            }
        @Override
        public N_BaseNeuron getNeuronProto() {
            return neuronFactory.getSelected();
            }
        @Override
        public boolean getLearningMode() {
            return p_LearningMode;
            }
        };
    private int count = 0;
    public void toLog(String ss){
        toLog(false,ss);
        }
    private void toLog(boolean high,String ss){
        count++;
        if (count==1000)
            Mes.setText("");
        Mes.append(ss+"\n");
        if (high)
            Mes2.setText(ss);
        }
    private void toLog(Throwable ee){
        count++;
        if (count==1000)
            Mes.setText("");
        Mes.append(ee.toString()+"\n");
        Mes2.setText(ee.toString());
        StackTraceElement ss[] = ee.getStackTrace();
        for (int i=0;i<5 && i< ss.length;i++)
            Mes.append(ss[i].toString()+"\n");
        }
    private void setViewState(){
        try {
        FileName.setText(p_lastFileName);
        M4.setText(""+p_OverProc);
        M5.setText(""+p_BlockSize);
        sourceFactory.setSelectedIndex(p_SampleType);
        SCompress.setValue(p_CompressStage);
        KCompress.setText(""+p_CompressStage);
        ToneCount.setText(""+p_SubToneCount);
        GPUmode.setSelectedIndex(p_GPUmode);
        modelFactory.setSelectedName(p_ModelId);
        neuronFactory.setSelectedName(p_NeuronId);
        layerFactory.setSelectedName(p_LayerId);
        int vv = (int)(p_Ampl*100);
        FilterDeep.setText(""+vv);
        KAmpl.setValue(vv);
        FilterMode.setSelectedIndex(p_FilterMode);
        LogLevel.setSelectedIndex(p_LogLevel);
        Gammatone.setVisible(p_GTFEnabled);
        NGammatone.setVisible(p_GTFEnabled);
        GammaLabel.setVisible(p_GTFEnabled);
        Gammatone.setValue(p_GTFNote);
        NGammatone.setText(fft.getNoteNameByIndex(p_GTFNote));
        ModelSourceType.setSelectedIndex(p_ModelSourceType);
        KSmooth.setText(""+kSmooth);
        StatSmooth.setSelected(statSmooth);
        StatMiddle1.setSelected(statMiddle);
        StatDiff.setSelected(statDisp);
        StatDiffT.setSelected(statDiffT);
        StatDiffF.setSelected(statDiffF);
        FirstN.setText(""+nFirstMax);
        RemoveExp.setSelected(removeExp);
        NPointsExp.setText(""+nFirstExpPoints);
        NoFirst.setText(""+noFirstPoints);
        NoLast.setText(""+noLastPoints);
        KMultiple.setText(String.format("%2.1f",kMultiple));
        NTrendPoints.setText(""+nTrendPoints);
        } catch(Throwable ee){
            toLog(ee);
            }
        }
    private boolean getViewState(){
        try {
        p_OverProc = Integer.parseInt(M4.getText());
        p_BlockSize = Integer.parseInt(M5.getText());
        p_CompressStage = Integer.parseInt(KCompress.getText());
        p_SampleType = sourceFactory.getSelectedIndex();
        p_SubToneCount = Integer.parseInt(ToneCount.getText());
        p_GPUmode = GPUmode.getSelectedIndex();
        p_ModelId = modelFactory.getSelectedName();
        p_NeuronId = neuronFactory.getSelectedName();
        p_LayerId = neuronFactory.getSelectedName();
        p_Ampl = (float)(Integer.parseInt(FilterDeep.getText())/100.);
        p_FilterMode = FilterMode.getSelectedIndex();
        p_GTFNote = Gammatone.getValue();
        p_ModelSourceType=ModelSourceType.getSelectedIndex();
        p_LogLevel = LogLevel.getSelectedIndex();
        kSmooth = Integer.parseInt(KSmooth.getText());
        statSmooth = StatSmooth.isSelected();
        statMiddle = StatMiddle1.isSelected();
        statDisp = StatDiff.isSelected();
        statDiffT =StatDiffT.isSelected();
        statDiffF = StatDiffF.isSelected();
        nFirstMax = Integer.parseInt(FirstN.getText());
        removeExp = RemoveExp.isSelected();
        nFirstExpPoints = Integer.parseInt(FirstN.getText());
        noFirstPoints = Integer.parseInt(NoFirst.getText());
        noLastPoints = Integer.parseInt(NoLast.getText());
        String ss = KMultiple.getText();
        kMultiple = Double.parseDouble(ss.replace(",","."));
        nTrendPoints = Integer.parseInt(NTrendPoints.getText());
        return true;
            } catch (Exception ee){ toLog("Ошибка формата целого/вещественного");
                return false; }
    }

    private boolean getItem(int menu, int idx){
        JMenu settings = this.getJMenuBar().getMenu(menu);
        return ((JCheckBoxMenuItem)settings.getItem(idx)).isSelected();
        }
    private void setItem(int menu, int idx, boolean vv){
        JMenu settings = this.getJMenuBar().getMenu(menu);
        ((JCheckBoxMenuItem)settings.getItem(idx)).setSelected(vv);
        }
    private void setItemEnabled(int menu, int idx, boolean vv){
        JMenu settings = this.getJMenuBar().getMenu(menu);
        ((JCheckBoxMenuItem)settings.getItem(idx)).setEnabled(vv);
        }
    public void setMenuState(){
        setItem(1,0,p_RealTime);
        setItem(1,1,p_Play);
        setItemEnabled(1,1,p_RealTime);
        setItem(2,0,p_ShowAmplSpector);
        setItem(1,2,p_Compress);
        setItem(1,3,p_LogFreq);
        setItem(1,4,p_Repeat);
        setItem(1,5,p_PlayFilter);
        setItem(2,1,p_Filtered);
        setItem(2,2,p_GTFEnabled);
        setItem(2,3,p_SrcSpectrum);
        setItem(2,4,p_Cohleogram);
        setItem(2,5,p_OutAmpl);
        setItem(1,7,p_LearningMode);
        setItem(1,8,p_MultipleSK);
        setItem(1,9,p_FFTWindowReduce);
        setItem(2,8,p_White);
        setItem(2,7,p_GraphFX);
        setItem(1,6,p_GPU);         // Должно быть последним
        }
    private void getMenuState(){
        p_RealTime = getItem(1,0);
        p_Play = getItem(1,1);
        setItemEnabled(1,1,p_RealTime);
        p_ShowAmplSpector = getItem(2,0);        
        if (!p_ShowAmplSpector && panels[0]!=null){
            panels[0].dispose();
            panels[0]=null;
            }
        p_Compress =getItem(1,2);
        SCompress.setVisible(p_Compress);
        KCompress.setVisible(p_Compress);
        LCompress.setVisible(p_Compress);
        p_LogFreq = getItem(1,3);
        p_Repeat = getItem(1,4);
        p_PlayFilter = getItem(1,5);
        p_Filtered = getItem(2,1);
        p_GTFEnabled = getItem(2,2);
        p_SrcSpectrum = getItem(2,3);
        p_Cohleogram = getItem(2,4);
        p_LearningMode = getItem(1,7);
        p_MultipleSK = getItem(1,8);
        p_OutAmpl = getItem(2,5);
        p_White = getItem(2,8);
        p_FFTWindowReduce = getItem(1,9);
        p_GraphFX = getItem(2,7);
        p_GPU = getItem(1,6);         // Должно быть последним
        saveSettings();
        setViewState();
        }
    @Override
    synchronized public void release(LayerWindow src) {
        for(int i=0;i<panels.length;i++)
        if (src == panels[i]){
            panels[i]=null;
            break;
            }
        }
    public FFTView() {
        initComponents();
        this.setBounds(30,20, 900, 680);
        setTitle("CASA");
        FileName.setEditable(false);
        neuronFactory = new BoxFactory<N_BaseNeuron>(new NeuronFactory(),NeuronId,
            new BoxFactory.BoxFactoryCallBack<N_BaseNeuron>(){
                @Override
                public void getSelected(N_BaseNeuron selectedItem) {
                    p_NeuronId = neuronFactory.getSelectedName();
                    }
                });    
        layerFactory = new BoxFactory<NL_Integrate>(new LayerFactory(),LayerId,
            new BoxFactory.BoxFactoryCallBack<NL_Integrate>(){
                @Override
                public void getSelected(NL_Integrate selectedItem) {                    
                    p_LayerId = layerFactory.getSelectedName();
                    }
                });    
        factory =  new ModelFactory();
        modelFactory = new BoxFactory<I_LayerModel>(factory,ModelId,
            new BoxFactory.BoxFactoryCallBack<I_LayerModel>(){
                @Override
                public void getSelected(I_LayerModel selectedItem) {                    
                    p_ModelId = modelFactory.getSelectedName();
                    setStatList();
                    }
                });    
        sourceFactory = new BoxFactory<FFTAudioSource>(new AudioSourceFactory(),Samples,
            new BoxFactory.BoxFactoryCallBack<FFTAudioSource>(){
                @Override
                public void getSelected(FFTAudioSource selectedItem) {                    
                    p_SampleType = sourceFactory.getSelectedIndex();
                    }
                });    
        loadSettings();
        setMenuState();
        }
    public void clearStatistic(){
        inputStat.reset();
        if (statFactory==null)
            return;
        for (LayerStatistic stat : statFactory.getFactory().getFactoryContent())
            stat.reset();
        }
    private void setStatList(){
        setStatList(null);
        }
    private void setStatList(TypeFactory<LayerStatistic> fac2){
        if (fac2 == null){
            I_LayerModel mod = modelFactory.getSelected();
            if (mod == null)
                return;
            fac2 = mod.getFactory();
            }
        statFactory = new BoxFactory<LayerStatistic>(fac2,Statistics,
            new BoxFactory.BoxFactoryCallBack<LayerStatistic>(){
                @Override
                    public void getSelected(LayerStatistic selectedItem) {                    
                        selectedStat = selectedItem;
                        }   
                    });        
        }
    public void loadSettings(){
        DataInputStream out=null;
        try {
            out = new  DataInputStream(new FileInputStream(settingsFileName));
            p_RealTime = out.readBoolean();
            p_Play = out.readBoolean();
            p_ShowAmplSpector = out.readBoolean();  
            p_lastFileName = out.readUTF();
            p_lastFileDir = out.readUTF();
            p_SampleType = out.readInt();
            p_OverProc = out.readInt();
            p_BlockSize = out.readInt();
            p_Compress = out.readBoolean();
            p_CompressStage = out.readInt();
            p_SubToneCount = out.readInt();
            p_LogFreq = out.readBoolean();
            p_Repeat = out.readBoolean();
            p_ModelId = out.readUTF();
            p_NeuronId = out.readUTF();
            p_LayerId = out.readUTF();
            p_PlayFilter = out.readBoolean();
            p_Ampl = out.readFloat();
            p_Filtered = out.readBoolean();
            p_FilterMode = out.readInt();
            p_GTFEnabled = out.readBoolean();
            p_GTFNote = out.readInt();
            p_Cohleogram = out.readBoolean();
            p_ModelSourceType=out.readInt();
            p_SrcSpectrum = out.readBoolean();
            p_GPU = out.readBoolean();
            p_LearningMode = out.readBoolean();
            p_GraphFX = out.readBoolean();
            p_LogLevel = out.readInt();
            p_MultipleSK = out.readBoolean();
            p_White = out.readBoolean();
            p_OutAmpl = out.readBoolean();
            p_FFTWindowReduce = out.readBoolean();
            p_GPUmode = out.readInt();
            kSmooth = out.readInt();
            statSmooth = out.readBoolean();
            statMiddle = out.readBoolean();
            statDisp = out.readBoolean();
            statDiffT = out.readBoolean();
            statDiffF = out.readBoolean();
            nFirstMax = out.readInt();
            removeExp = out.readBoolean();
            nFirstExpPoints = out.readInt();
            noFirstPoints = out.readInt();
            noLastPoints = out.readInt();
            kMultiple = out.readDouble();
            nTrendPoints = out.readInt();
            setViewState();
            setMenuState();
            } catch (Exception ee){
                Mes2.setText("Настройки не загружены: "+ee.getMessage());
                toLog("Настройки не загружены: "+ee.getMessage());
                try { if (out!=null) out.close(); } catch (Exception e2){}
                }
        }
    public void saveSettings(){
        DataOutputStream out=null;
        try {
            out = new  DataOutputStream(new FileOutputStream(settingsFileName));
            out.writeBoolean(p_RealTime);
            out.writeBoolean(p_Play);
            out.writeBoolean(p_ShowAmplSpector);            
            out.writeUTF(p_lastFileName);
            out.writeUTF(p_lastFileDir);
            out.writeInt(p_SampleType);
            out.writeInt(p_OverProc);
            out.writeInt(p_BlockSize);
            out.writeBoolean(p_Compress);
            out.writeInt(p_CompressStage);
            out.writeInt(p_SubToneCount);
            out.writeBoolean(p_LogFreq);
            out.writeBoolean(p_Repeat);
            out.writeUTF(p_ModelId);
            out.writeUTF(p_NeuronId);
            out.writeUTF(p_LayerId);
            out.writeBoolean(p_PlayFilter);
            out.writeFloat(p_Ampl);
            out.writeBoolean(p_Filtered);
            out.writeInt(p_FilterMode);
            out.writeBoolean(p_GTFEnabled);
            out.writeInt(p_GTFNote);
            out.writeBoolean(p_Cohleogram);
            out.writeInt(p_ModelSourceType);
            out.writeBoolean(p_SrcSpectrum);
            out.writeBoolean(p_GPU);
            out.writeBoolean(p_LearningMode);
            out.writeBoolean(p_GraphFX);
            out.writeInt(p_LogLevel);
            out.writeBoolean(p_MultipleSK);
            out.writeBoolean(p_White);
            out.writeBoolean(p_OutAmpl);
            out.writeBoolean(p_FFTWindowReduce);
            out.writeInt(p_GPUmode);
            out.writeInt(kSmooth);
            out.writeBoolean(statSmooth);
            out.writeBoolean(statMiddle);
            out.writeBoolean(statDisp);
            out.writeBoolean(statDiffT);
            out.writeBoolean(statDiffF);
            out.writeInt(nFirstMax);
            out.writeBoolean(removeExp);
            out.writeInt(nFirstExpPoints);
            out.writeInt(noFirstPoints);
            out.writeInt(noLastPoints);
            out.writeDouble(kMultiple);
            out.writeInt(nTrendPoints);
            } catch (Exception ee){
                toLog(true,"Настройки не сохранены");
                try { if (out!=null) out.close(); } catch (Exception e2){}
                }
        }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FilterMode = new javax.swing.JComboBox();
        Run = new javax.swing.JButton();
        M2 = new javax.swing.JTextField();
        M3 = new javax.swing.JTextField();
        M1 = new javax.swing.JTextField();
        M4 = new javax.swing.JTextField();
        GammaLabel = new javax.swing.JLabel();
        M5 = new javax.swing.JTextField();
        FileName = new javax.swing.JTextField();
        File = new javax.swing.JButton();
        LCompress = new javax.swing.JLabel();
        Samples = new javax.swing.JComboBox();
        Time = new javax.swing.JTextField();
        Pause = new javax.swing.JButton();
        NSynapses = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        MaxAmpl = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        KCompress = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        SCompress = new javax.swing.JSlider();
        ModelId = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        FilterDeep = new javax.swing.JTextField();
        NNeightbors = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        NeuronId = new javax.swing.JComboBox();
        KAmpl = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        ToneCount = new javax.swing.JTextField();
        Gammatone = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        NGammatone = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ModelSourceType = new javax.swing.JComboBox();
        Mes2 = new javax.swing.JTextField();
        Ampl = new javax.swing.JSlider();
        LayerId = new javax.swing.JComboBox();
        LCompress1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Mes = new java.awt.TextArea();
        jLabel16 = new javax.swing.JLabel();
        LogLevel = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        ShowStatistic = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        Statistics = new javax.swing.JComboBox();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel19 = new javax.swing.JLabel();
        GPUmode = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        PlayMPX = new javax.swing.JButton();
        InputStatistic = new javax.swing.JButton();
        KMultiple = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        KSmooth = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        StatSmooth = new javax.swing.JCheckBox();
        StatDiffT = new javax.swing.JCheckBox();
        StatMiddle1 = new javax.swing.JCheckBox();
        StatDiff = new javax.swing.JCheckBox();
        StatDiffF = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        FirstN = new javax.swing.JTextField();
        RemoveExp = new javax.swing.JCheckBox();
        NPointsExp = new javax.swing.JTextField();
        NoFirst = new javax.swing.JTextField();
        NoLast = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        NTrendPoints = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        LoadModel = new javax.swing.JMenuItem();
        SaveModel = new javax.swing.JMenuItem();
        SaveXML = new javax.swing.JMenuItem();
        Settings = new javax.swing.JMenu();
        SettingRealTime = new javax.swing.JCheckBoxMenuItem();
        SettingPlayer = new javax.swing.JCheckBoxMenuItem();
        SettingCompress = new javax.swing.JCheckBoxMenuItem();
        SettingLogariphmic = new javax.swing.JCheckBoxMenuItem();
        SettingRepeat = new javax.swing.JCheckBoxMenuItem();
        SettingPlayerFilter = new javax.swing.JCheckBoxMenuItem();
        SettingGPU = new javax.swing.JCheckBoxMenuItem();
        SettingLearning = new javax.swing.JCheckBoxMenuItem();
        SettingFC = new javax.swing.JCheckBoxMenuItem();
        SettingFFTWindowReduce = new javax.swing.JCheckBoxMenuItem();
        Вид = new javax.swing.JMenu();
        ViewInputAmplitude = new javax.swing.JCheckBoxMenuItem();
        ViewFilteredSpector = new javax.swing.JCheckBoxMenuItem();
        ViewGammaton = new javax.swing.JCheckBoxMenuItem();
        ViewSoucrceSpector = new javax.swing.JCheckBoxMenuItem();
        ViewCohleogram = new javax.swing.JCheckBoxMenuItem();
        ViewAmplitudes = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        StatisticView = new javax.swing.JCheckBoxMenuItem();
        WhiteBack = new javax.swing.JCheckBoxMenuItem();
        Операции = new javax.swing.JMenu();
        PreloadSpector = new javax.swing.JMenuItem();
        PreloadCohleogram = new javax.swing.JMenuItem();
        PreloadClear = new javax.swing.JMenuItem();
        TextToWaveConvert = new javax.swing.JMenuItem();
        ExportBin = new javax.swing.JMenuItem();
        ExportJSON = new javax.swing.JMenuItem();
        Info = new javax.swing.JMenu();
        InfoGPU = new javax.swing.JMenuItem();

        FilterMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Вых.слой", "Пустой", "Линейный", "Жесткий" }));
        FilterMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                FilterModeItemStateChanged(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        Run.setText("Старт");
        Run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunActionPerformed(evt);
            }
        });
        getContentPane().add(Run);
        Run.setBounds(10, 10, 63, 23);
        getContentPane().add(M2);
        M2.setBounds(230, 90, 50, 25);
        getContentPane().add(M3);
        M3.setBounds(230, 40, 50, 25);
        getContentPane().add(M1);
        M1.setBounds(150, 90, 50, 25);

        M4.setText("75");
        getContentPane().add(M4);
        M4.setBounds(80, 10, 30, 25);

        GammaLabel.setText("Гамматон");
        getContentPane().add(GammaLabel);
        GammaLabel.setBounds(220, 510, 70, 14);

        M5.setText("16");
        M5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M5ActionPerformed(evt);
            }
        });
        getContentPane().add(M5);
        M5.setBounds(250, 10, 30, 25);

        FileName.setEditable(false);
        FileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileNameActionPerformed(evt);
            }
        });
        getContentPane().add(FileName);
        FileName.setBounds(80, 120, 200, 25);

        File.setText("Файл");
        File.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileActionPerformed(evt);
            }
        });
        getContentPane().add(File);
        File.setBounds(10, 120, 59, 23);

        LCompress.setText("Компрессия");
        getContentPane().add(LCompress);
        LCompress.setBounds(70, 195, 75, 14);

        getContentPane().add(Samples);
        Samples.setBounds(10, 90, 130, 25);
        getContentPane().add(Time);
        Time.setBounds(150, 40, 70, 25);

        Pause.setText("Пауза");
        Pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PauseActionPerformed(evt);
            }
        });
        getContentPane().add(Pause);
        Pause.setBounds(10, 40, 63, 23);

        NSynapses.setText("5");
        NSynapses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NSynapsesActionPerformed(evt);
            }
        });
        getContentPane().add(NSynapses);
        NSynapses.setBounds(210, 460, 30, 25);

        jLabel4.setText("Время (мс)");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(80, 45, 70, 14);

        MaxAmpl.setText("0");
        MaxAmpl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaxAmplActionPerformed(evt);
            }
        });
        getContentPane().add(MaxAmpl);
        MaxAmpl.setBounds(10, 260, 50, 25);

        jLabel5.setText("FFT+GTF");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(230, 70, 60, 14);

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(70, 260, 30, 30);

        KCompress.setText("15");
        getContentPane().add(KCompress);
        KCompress.setBounds(10, 190, 30, 25);

        jLabel8.setText("Пар соседей");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(130, 480, 90, 14);

        SCompress.setMaximum(30);
        SCompress.setValue(15);
        SCompress.setFocusTraversalPolicyProvider(true);
        SCompress.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SCompressStateChanged(evt);
            }
        });
        getContentPane().add(SCompress);
        SCompress.setBounds(150, 190, 130, 23);

        ModelId.setToolTipText("");
        ModelId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModelIdActionPerformed(evt);
            }
        });
        getContentPane().add(ModelId);
        ModelId.setBounds(90, 330, 190, 25);

        jLabel9.setText("Вид фильтрации");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(10, 565, 100, 14);

        FilterDeep.setText("0");
        getContentPane().add(FilterDeep);
        FilterDeep.setBounds(110, 530, 30, 25);

        NNeightbors.setText("2");
        NNeightbors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NNeightborsActionPerformed(evt);
            }
        });
        getContentPane().add(NNeightbors);
        NNeightbors.setBounds(250, 460, 30, 25);

        jLabel6.setText("1024 *");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(210, 15, 40, 14);

        jLabel7.setText("Шаг (мс)");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(160, 70, 60, 14);

        NeuronId.setToolTipText("");
        NeuronId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NeuronIdActionPerformed(evt);
            }
        });
        getContentPane().add(NeuronId);
        NeuronId.setBounds(90, 360, 190, 25);

        KAmpl.setMinimum(-100);
        KAmpl.setValue(0);
        KAmpl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                KAmplStateChanged(evt);
            }
        });
        getContentPane().add(KAmpl);
        KAmpl.setBounds(150, 530, 130, 23);

        jLabel11.setText("режим GPU ");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(10, 160, 80, 20);

        ToneCount.setText("4");
        getContentPane().add(ToneCount);
        ToneCount.setBounds(250, 160, 30, 25);

        Gammatone.setMaximum(119);
        Gammatone.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                GammatoneStateChanged(evt);
            }
        });
        Gammatone.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                GammatoneCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(Gammatone);
        Gammatone.setBounds(150, 500, 130, 23);

        jLabel12.setText("Вх. сигналов");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(130, 460, 70, 14);

        NGammatone.setEditable(false);
        NGammatone.setText("C контр");
        NGammatone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NGammatoneActionPerformed(evt);
            }
        });
        getContentPane().add(NGammatone);
        NGammatone.setBounds(60, 500, 80, 25);

        jLabel3.setText("% перекрытия");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(120, 15, 100, 14);

        ModelSourceType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Спектр", "Кохлеограмма", "Спектр*Кохлеограмма" }));
        getContentPane().add(ModelSourceType);
        ModelSourceType.setBounds(90, 300, 190, 25);
        getContentPane().add(Mes2);
        Mes2.setBounds(10, 590, 810, 30);

        Ampl.setMaximum(1000);
        Ampl.setValue(500);
        Ampl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AmplStateChanged(evt);
            }
        });
        getContentPane().add(Ampl);
        Ampl.setBounds(150, 230, 130, 23);

        getContentPane().add(LayerId);
        LayerId.setBounds(90, 390, 190, 25);

        LCompress1.setText("Уровень");
        getContentPane().add(LCompress1);
        LCompress1.setBounds(70, 230, 75, 14);

        jLabel2.setText("Ампл.");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 290, 50, 14);

        jLabel10.setText("Трасса");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(120, 265, 50, 14);

        jLabel13.setText("Модель");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(10, 340, 50, 14);

        jLabel14.setText("Нейрон");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(10, 370, 50, 14);

        jLabel15.setText("Статистика");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(10, 430, 70, 14);
        getContentPane().add(Mes);
        Mes.setBounds(470, 10, 410, 570);

        jLabel16.setText("Глубина фильтр.");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(10, 540, 100, 14);

        LogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Все", "Информация", "Предупреждения", "Ошибки", "Фатальные" }));
        LogLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogLevelItemStateChanged(evt);
            }
        });
        getContentPane().add(LogLevel);
        LogLevel.setBounds(166, 260, 110, 25);

        jLabel17.setText("Вход");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(10, 310, 50, 14);

        ShowStatistic.setText("<");
        ShowStatistic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowStatisticActionPerformed(evt);
            }
        });
        getContentPane().add(ShowStatistic);
        ShowStatistic.setBounds(240, 420, 40, 30);

        jLabel18.setText("Слой");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(10, 400, 50, 14);

        getContentPane().add(Statistics);
        Statistics.setBounds(90, 420, 140, 25);

        jToggleButton1.setText("*");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButton1);
        jToggleButton1.setBounds(10, 500, 40, 30);

        jLabel19.setText("Дискр. ");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(180, 150, 50, 20);

        GPUmode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GPU", "CPU", "JTP" }));
        getContentPane().add(GPUmode);
        GPUmode.setBounds(90, 155, 60, 30);

        jLabel20.setText("полутона");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(180, 170, 60, 20);

        PlayMPX.setText("Старт mpx");
        PlayMPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PlayMPXActionPerformed(evt);
            }
        });
        getContentPane().add(PlayMPX);
        PlayMPX.setBounds(10, 65, 130, 23);

        InputStatistic.setText("Стат.входа");
        InputStatistic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputStatisticActionPerformed(evt);
            }
        });
        getContentPane().add(InputStatistic);
        InputStatistic.setBounds(300, 80, 150, 23);

        KMultiple.setText("3.0");
        getContentPane().add(KMultiple);
        KMultiple.setBounds(420, 10, 30, 25);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator2);
        jSeparator2.setBounds(288, 10, 10, 560);

        KSmooth.setText("50");
        getContentPane().add(KSmooth);
        KSmooth.setBounds(420, 40, 30, 25);

        jLabel21.setText("Первые N макс.");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(310, 200, 100, 20);

        StatSmooth.setSelected(true);
        StatSmooth.setText("Сглаживание");
        getContentPane().add(StatSmooth);
        StatSmooth.setBounds(300, 40, 110, 23);

        StatDiffT.setText("Неравн.по времени");
        getContentPane().add(StatDiffT);
        StatDiffT.setBounds(300, 170, 140, 23);

        StatMiddle1.setSelected(true);
        StatMiddle1.setText("Среднее");
        getContentPane().add(StatMiddle1);
        StatMiddle1.setBounds(300, 110, 140, 23);

        StatDiff.setText("СКО");
        getContentPane().add(StatDiff);
        StatDiff.setBounds(300, 130, 140, 23);

        StatDiffF.setText("Неравн. по частоте");
        getContentPane().add(StatDiffF);
        StatDiffF.setBounds(300, 150, 140, 23);

        jLabel22.setText("Спектр/гамматон");
        getContentPane().add(jLabel22);
        jLabel22.setBounds(300, 10, 110, 20);

        FirstN.setText("10");
        getContentPane().add(FirstN);
        FirstN.setBounds(410, 200, 40, 25);

        RemoveExp.setText("Фильтр exp");
        getContentPane().add(RemoveExp);
        RemoveExp.setBounds(300, 230, 100, 23);

        NPointsExp.setText("10");
        getContentPane().add(NPointsExp);
        NPointsExp.setBounds(410, 230, 40, 25);

        NoFirst.setText("0");
        getContentPane().add(NoFirst);
        NoFirst.setBounds(360, 280, 40, 25);

        NoLast.setText("0");
        getContentPane().add(NoLast);
        NoLast.setBounds(410, 280, 40, 25);

        jLabel1.setText("Отсечка слева/справа");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(310, 260, 150, 14);

        jLabel23.setText("Удаление тренда");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(310, 325, 100, 14);

        NTrendPoints.setText("0");
        getContentPane().add(NTrendPoints);
        NTrendPoints.setBounds(410, 320, 40, 25);

        jMenu2.setText("Модель");

        LoadModel.setText("Загрузить");
        LoadModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadModelActionPerformed(evt);
            }
        });
        jMenu2.add(LoadModel);

        SaveModel.setText("Сохранить");
        SaveModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveModelActionPerformed(evt);
            }
        });
        jMenu2.add(SaveModel);

        SaveXML.setText("Создать из XML");
        SaveXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveXMLActionPerformed(evt);
            }
        });
        jMenu2.add(SaveXML);

        jMenuBar1.add(jMenu2);

        Settings.setText("Настройки");

        SettingRealTime.setSelected(true);
        SettingRealTime.setText("Реальное время");
        SettingRealTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingRealTimeActionPerformed(evt);
            }
        });
        Settings.add(SettingRealTime);

        SettingPlayer.setSelected(true);
        SettingPlayer.setText("Плейер");
        SettingPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingPlayerActionPerformed(evt);
            }
        });
        Settings.add(SettingPlayer);

        SettingCompress.setSelected(true);
        SettingCompress.setText("Компрессия");
        SettingCompress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingCompressActionPerformed(evt);
            }
        });
        Settings.add(SettingCompress);

        SettingLogariphmic.setSelected(true);
        SettingLogariphmic.setText("Лог.шкала спектра");
        SettingLogariphmic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingLogariphmicActionPerformed(evt);
            }
        });
        Settings.add(SettingLogariphmic);

        SettingRepeat.setSelected(true);
        SettingRepeat.setText("Повторение");
        SettingRepeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingRepeatActionPerformed(evt);
            }
        });
        Settings.add(SettingRepeat);

        SettingPlayerFilter.setSelected(true);
        SettingPlayerFilter.setText("Плейер-фильтр");
        SettingPlayerFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingPlayerFilterActionPerformed(evt);
            }
        });
        Settings.add(SettingPlayerFilter);

        SettingGPU.setSelected(true);
        SettingGPU.setText("GPU");
        SettingGPU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SettingGPUItemStateChanged(evt);
            }
        });
        SettingGPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingGPUActionPerformed(evt);
            }
        });
        Settings.add(SettingGPU);

        SettingLearning.setSelected(true);
        SettingLearning.setText("Обучение");
        SettingLearning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingLearningActionPerformed(evt);
            }
        });
        Settings.add(SettingLearning);

        SettingFC.setSelected(true);
        SettingFC.setText("С*К (произведение)");
        SettingFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingFCActionPerformed(evt);
            }
        });
        Settings.add(SettingFC);

        SettingFFTWindowReduce.setSelected(true);
        SettingFFTWindowReduce.setText("FFT с редукцией окна");
        SettingFFTWindowReduce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingFFTWindowReduceActionPerformed(evt);
            }
        });
        Settings.add(SettingFFTWindowReduce);

        jMenuBar1.add(Settings);

        Вид.setText("Вид");

        ViewInputAmplitude.setSelected(true);
        ViewInputAmplitude.setText("Вход - амплитудный спектр");
        ViewInputAmplitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewInputAmplitudeActionPerformed(evt);
            }
        });
        Вид.add(ViewInputAmplitude);

        ViewFilteredSpector.setSelected(true);
        ViewFilteredSpector.setText("Отфильтрованный спектр");
        ViewFilteredSpector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewFilteredSpectorActionPerformed(evt);
            }
        });
        Вид.add(ViewFilteredSpector);

        ViewGammaton.setSelected(true);
        ViewGammaton.setText("Гамматон");
        ViewGammaton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewGammatonActionPerformed(evt);
            }
        });
        Вид.add(ViewGammaton);

        ViewSoucrceSpector.setSelected(true);
        ViewSoucrceSpector.setText("Исходный спектр");
        ViewSoucrceSpector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewSoucrceSpectorActionPerformed(evt);
            }
        });
        Вид.add(ViewSoucrceSpector);

        ViewCohleogram.setSelected(true);
        ViewCohleogram.setText("Кохлеограмма");
        ViewCohleogram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewCohleogramActionPerformed(evt);
            }
        });
        Вид.add(ViewCohleogram);

        ViewAmplitudes.setSelected(true);
        ViewAmplitudes.setText("Выход - амплитудный спектр");
        ViewAmplitudes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewAmplitudesActionPerformed(evt);
            }
        });
        Вид.add(ViewAmplitudes);
        Вид.add(jSeparator1);

        StatisticView.setSelected(true);
        StatisticView.setText("Статистика - график FX");
        StatisticView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StatisticViewActionPerformed(evt);
            }
        });
        Вид.add(StatisticView);

        WhiteBack.setSelected(true);
        WhiteBack.setText("Белый фон");
        WhiteBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WhiteBackActionPerformed(evt);
            }
        });
        Вид.add(WhiteBack);

        jMenuBar1.add(Вид);

        Операции.setText("Операции");

        PreloadSpector.setText("Спектр - preload");
        PreloadSpector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreloadSpectorActionPerformed(evt);
            }
        });
        Операции.add(PreloadSpector);

        PreloadCohleogram.setText("Кохлеограмма - preload");
        PreloadCohleogram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreloadCohleogramActionPerformed(evt);
            }
        });
        Операции.add(PreloadCohleogram);

        PreloadClear.setText("Очиcтить preload");
        PreloadClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreloadClearActionPerformed(evt);
            }
        });
        Операции.add(PreloadClear);

        TextToWaveConvert.setText("Конверсия wave из текста");
        TextToWaveConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextToWaveConvertActionPerformed(evt);
            }
        });
        Операции.add(TextToWaveConvert);

        ExportBin.setText("Экспорт в файл (mpx)");
        ExportBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportBinActionPerformed(evt);
            }
        });
        Операции.add(ExportBin);

        ExportJSON.setText("Экспорт JSON");
        ExportJSON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportJSONActionPerformed(evt);
            }
        });
        Операции.add(ExportJSON);

        jMenuBar1.add(Операции);

        Info.setText("Информация");

        InfoGPU.setText("GPU");
        InfoGPU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InfoGPUActionPerformed(evt);
            }
        });
        Info.add(InfoGPU);

        jMenuBar1.add(Info);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private int msOnStep=0;
    
    private FFTSpectorFilter emptyFilter = new FFTSpectorFilter(){
        @Override
        public Complex convert(Complex in,float filterValue) {
            return in;
            }
        };
    private FFTSpectorFilter hardFilter = new FFTSpectorFilter(){
        @Override
        public Complex convert(Complex in, float filterValue) {
            if (filterValue < GBL.FireMiddle)
                return new Complex(0,0);
            else
                return in.multiply(p_Ampl+1);
            }
        };
    private FFTSpectorFilter filter = new FFTSpectorFilter(){
        @Override
        public Complex convert(Complex in, float filterValue) {
            if (filterValue < GBL.FireMiddle)
                return in.multiply(1-p_Ampl);
            else
                return in.multiply(p_Ampl+1);
            }
        };
    private FFTSpectorFilter filters[]={
        null,                       // Выходной слой - спектр - волна
        emptyFilter,
        filter,
        hardFilter
        };    

    public boolean saveCurrentViewState(){
        boolean bb=getViewState();                       // Получить данные от View
        getMenuState();
        saveSettings();
        return bb;
        }

    private void runToPlay(Runnable code){
        saveCurrentViewState();
        synchronized (Pause){
            if (isRun){
                isRun = false;
                isPause=false;
                Pause.setText("Пауза");
                Run.setText("Старт");
                player.interruptPlay();
                Pause.notifyAll();               // Если ждет поток (пауза)
                return;
            }
            Run.setText("Стоп");
            isRun=true;
            repeat=false;
            Thread tt = new Thread(play);
            tt.setPriority(Thread.MIN_PRIORITY);
            tt.start();
            }
        }


    private void RunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunActionPerformed
        clearStatistic();
        runToPlay(play);
    }//GEN-LAST:event_RunActionPerformed
    private I_NeuronStep stepCB = new I_NeuronStep(){       // Шаг модели
        @Override
        public void onFire(N_BaseNeuron nw) {
            if (p_LogLevel==GBL.MesTrass)
                toLog("Сработал - "+nw.getUID());
            }
        @Override
        public void onMessage(int level, String mes) {
            if (level >= p_LogLevel)
                toLog(level>=GBL.MesFatal,mes);
            }
        };  
    private boolean needCohle(){
        return p_Cohleogram || (modelFactory.getSelected()!=null  && p_ModelSourceType!=0);
        }
    //--------------------------------------------------------------------------
    private FFTCallBack back = new FFTCallBack(){
                @Override
                public void onStart(float msOnStep) {
                    FFTView.this.msOnStep = (int)msOnStep;
                    M1.setText("" + (int)msOnStep);
                    lastClock = new Date().getTime();
                    }
                @Override
                public void onFinish() {
                    //fft.test();
                    if (p_Repeat){
                        if (isRun && !isPause){
                            repeat=true;
                            Thread tt = new Thread(play);
                            tt.setPriority(Thread.MIN_PRIORITY);
                            tt.start();
                            }
                        }
                    else{
                        System.out.println("Закончил");
                        isRun = false;
                        Run.setText("Старт");
                        }
                    }
                @Override
                public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft) {
                    try {
                        long tt = System.currentTimeMillis();
                        boolean log = p_LogFreq;
                        float spectrum[] = fft.getLogSpectrum();
                        float lineSpectrum[] = fft.getSpectrum();
                        boolean xx;
                        try {
                            synchronized (FFTView.this) {
                                float out[] = null;
                                float cohle[] = null;
                                float multiple[] = null;
                                if (needCohle()) {
                                    cohle = fft.getGTSpectrum();
                                    multiple = fft.getMultipleSpectrum(p_MultipleSK);
                                    }
                                //--------------------------------------------------------------------------
                                switch(ModelSourceType.getSelectedIndex()){
                                    case 0:
                                        inputStat.addStatistic(fft.isLogFreqMode() ? spectrum : lineSpectrum);
                                        break;
                                    case 1:
                                        if (needCohle())
                                            inputStat.addStatistic(cohle);
                                        break;
                                    case 2:
                                        if (needCohle())
                                            inputStat.addStatistic(multiple);
                                        break;
                                    }
                                if (panels[1] != null){
                                    if (fft.isLogFreqMode())
                                        panels[1].paint(spectrum, fft.getSubToneCount());
                                    else
                                        panels[1].paint(lineSpectrum);
                                    }
                                if (panels[0] != null)
                                    panels[0].paint(fft);
                                if (panels[4] != null && fft.validGammatone())        // Огибающая спектра  = true
                                    panels[4].paint(fft.getGammatone(p_GTFNote, false), "");
                                if (panels[5] != null && cohle != null)
                                    panels[5].paint(cohle, fft.getParams());
                                if (panels[6] != null){
                                    panels[6].paint(multiple, fft.getParams());
                                    }
                                fft.addCount(6);
                                if (modelFactory.getSelected() != null) {
                                    float src[] = spectrum;
                                    if (p_ModelSourceType != 0) {
                                        if (p_ModelSourceType == 1)
                                            src = cohle;
                                        else
                                            src = multiple;
                                        }
                                    out = model.step(src, stepCB);
                                    fft.addCount(5);
                                    if (p_PlayFilter) {
                                        float vv[] = fft.convertToWave(out, filters[p_FilterMode]);
                                        player.addToPlay(vv, p_OverProc, 0.1F);
                                        }
                                    if (panels[3] != null) {
                                        float bb[] = fft.getFilteredSpectrum(out, filters[p_FilterMode]);
                                        panels[3].paint(bb, fft.getSubToneCount());
                                        }
                                    if (panels[2] != null)
                                        panels[2].paint(out, model.getSubToneCount());
                                    if (panels[8] != null)
                                        panels[8].paint(out);
                                    fft.addCount(6);
                                }
                            }
                        } catch (Exception ex) {
                            toLog(ex);
                        }
                        M2.setText("" + calcMS);
                        M3.setText("" + nBlock);
                        Time.setText("" + (int) totalMS);
                        MaxAmpl.setText("" + fft.getMaxAmpl());
                        fft.setLogFreqMode(p_LogFreq);
                        fft.setCompressMode(p_Compress);
                        fft.setCompressGrade(Integer.parseInt(KCompress.getText()));
                        if (p_RealTime) {
                            try {
                                //---------- Задержа от плейера или вычисляемая
                                int delay = 0;
                                FFTAudioSource ais = fft.getAudioInputStream();
                                if (ais != null && ais.isPlaying()) {
                                    delay = (int) (totalMS - ais.getCurrentPlayTimeMS());
                                    if (delay <= 0)
                                        toLog("Блок " + nBlock + " играет быстрее на " + (-delay));
                                    else
                                        Thread.sleep(delay);
                                } else {
                                    //int t2 = (int)(calcMS+(new Date().getTime()-tt));
                                    //int delay = (int)(msOnStep-t2);
                                    long t2 = System.currentTimeMillis();     // Считать период по onStep
                                    long tt2 = t2 - lastClock;
                                    delay = (int) (msOnStep - tt2);
                                    if (delay <= 0)
                                        toLog("Блок " + nBlock + " задержка " + tt2 + ">" + msOnStep);
                                    else
                                        Thread.sleep(delay);
                                }
                            } catch (InterruptedException ex) {
                            }
                            lastClock = System.currentTimeMillis();
                            if (fft.getAudioInputStream()!=null)
                                fft.getAudioInputStream().play((int) totalMS, msOnStep);
                        }
                    } catch (Exception e){
                        toLog(createFatalMessage(e));
                        }
                    //---------------- Проверка пауза/остановка----------------
                    synchronized(Pause){
                        if (isPause)
                            try {
                                Pause.wait();
                                } catch (InterruptedException ex) {}
                        }
                    return isRun;
                    }
                @Override
                public void onError(Exception ee) {
                    toLog(true,"1."+createFatalMessage(ee));
                    }
                @Override
                public void onMessage(String mes) {
                    toLog(mes);
                    }
                };    
    //--------------------------------------------------------------------------
    Runnable play = new Runnable(){
        public void run(){
            FFTAudioSource src = null;
            inputStat.reset();
            src = sourceFactory.getSelected();
            if (src instanceof FFTAudioTextFile) {
                ((FFTAudioTextFile)src).setnPoints(nTrendPoints);
                }
            if (src instanceof FFTFileSource){
                FFTFileSource file  =  (FFTFileSource)src;
                boolean ff = file.testAndOpenFile(FFTAudioFile.OpenAndPlay, p_lastFileDir+p_lastFileName,FFT.sizeHZ, back);
                file.enableToPlay(p_RealTime && p_Play);
                if (!ff) {
                    toLog("Файл не открылся\n");
                    return;
                    }
                src = file;
                }
            try {
                if (repeat)
                    repeat=false;
                else{
                    fft.setFFTParams(new FFTParams(p_BlockSize*FFT.Size0,p_OverProc,
                        p_LogFreq,p_SubToneCount,
                        needCohle(),
                        p_GPU,p_FFTWindowReduce,p_GPUmode,kMultiple));
                    if (panels[1] == null && p_SrcSpectrum){
                        panels[1] = new NeuronLayerWindow(0,FFTView.this,600,"Исходный спектр",p_White);        
                        }
                    if (panels[5] == null && p_Cohleogram){
                        panels[5] = new NeuronLayerWindow(5,FFTView.this,600,"Кохлеограмма",p_White);        
                        }
                    if (panels[6] == null && p_Cohleogram){
                        panels[6] = new NeuronLayerWindow(6,FFTView.this,600,"Спектр*Кохлеограмма",p_White);
                        }
                    if (panels[0]==null && p_ShowAmplSpector){
                        panels[0] = new FFTLayerWindow(3,FFTView.this,350,"Спектр (амплитуда)",p_White);
                        }
                    if (panels[4]==null && p_GTFEnabled){
                        panels[4] = new FFTLayerWindow(4,FFTView.this,350,"Гамма-тон",p_White);
                        }
                    if (modelFactory.getSelected()!=null){
                        if (panels[3]==null && p_Filtered){
                            panels[3] = new NeuronLayerWindow(2,FFTView.this,600,"Отфильтрованный спектр",p_White);
                            }
                        if (panels[2]==null){
                            panels[2] = new NeuronLayerWindow(1,FFTView.this,600,(String)ModelId.getSelectedItem(),p_White);
                            }
                        if (panels[8]==null && p_OutAmpl){
                            panels[8] = new FFTLayerWindow(8,FFTView.this,350,"Выходной слой (амплитуда)",true,fft.getSubToneCount(),p_White);
                            }
                        model = modelFactory.getSelected();
                        model.initModel(fft.getSubToneCount(),paramsAdapter);
                        setStatList(model.getFactory());
                        model.reset(fft.getParams());
                        }
                    for(int i=0;i<panels.length;i++)
                        if (panels[i]!=null)
                            panels[i].reset();
                    }
                if (fft.isPreload())
                    fft.fftDirect(back);
                else
                    fft.fftDirect(src,back);   
                } catch (Exception ex) { 
                    toLog(ex);     
                    }
            }
        };
    
    private void M5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_M5ActionPerformed

    private void FileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileActionPerformed
        if (!(sourceFactory.getSelected() instanceof FFTFileSource)){
            Mes.append("Выберите файл в списке источников\n");
            return;
            }
        FileDialog dlg=new FileDialog(this,"Звуковой файл",FileDialog.LOAD);
        String ex = ((String)Samples.getSelectedItem()).equals("Файл") ? "wav" : "txt";
        dlg.setFile("*."+ex);
        dlg.show();
        String ss1 = dlg.getDirectory();
        String ss2 = dlg.getFile();
        if (ss1==null || ss2==null)
            FileName.setText("");
        else{
            FFTFileSource file = (FFTFileSource)sourceFactory.getSelected();
            boolean ff = file.testAndOpenFile(FFTAudioFile.Test,ss1+ss2, FFT.sizeHZ,new FFTCallBack(){
                @Override
                public void onStart(float msOnStep) {
                    }
                @Override
                public void onFinish() {
                    }
                @Override
                public boolean onStep(int nBlock, int msDelay, float totalMS, FFT fft) {
                    return false;
                    }
                @Override
                public void onError(Exception ee) {
                    toLog(true,"3."+createFatalMessage(ee));
                    }
                @Override
                public void onMessage(String mes) {
                    toLog(mes);
                    }
                });
            fft.close(null);
            FileName.setText(ff ? ss2 : "");
            if (ff) 
                p_lastFileName = ss2;
                p_lastFileDir = ss1;
            }
    }//GEN-LAST:event_FileActionPerformed

    private void PauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PauseActionPerformed
        if (!isRun){
            Pause.setText("Пауза");
            isPause=false;
            return;
            }
        isPause = !isPause;
        if (isPause){
            fft.getAudioInputStream().pause();
            Pause.setText("Прод.");
            }
        else{
            synchronized(Pause){
                Pause.notifyAll();
                }
            Pause.setText("Пауза");
            }
    }//GEN-LAST:event_PauseActionPerformed

    private void MaxAmplActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaxAmplActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MaxAmplActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        fft.clearMaxAmpl();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void SCompressStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SCompressStateChanged
        p_CompressStage = SCompress.getValue();
        setViewState();
    }//GEN-LAST:event_SCompressStateChanged

    private void NSynapsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NSynapsesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NSynapsesActionPerformed

    private void NNeightborsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NNeightborsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NNeightborsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!getViewState())
            return;
        saveSettings();
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void FileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FileNameActionPerformed

    private void SettingRealTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingRealTimeActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingRealTimeActionPerformed

    private void SettingPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingPlayerActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingPlayerActionPerformed

    private void ViewInputAmplitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewInputAmplitudeActionPerformed
        getMenuState();
    }//GEN-LAST:event_ViewInputAmplitudeActionPerformed

    private void SettingCompressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingCompressActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingCompressActionPerformed

    private void SettingRepeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingRepeatActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingRepeatActionPerformed

    private void SettingLogariphmicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingLogariphmicActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingLogariphmicActionPerformed

    private void SettingPlayerFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingPlayerFilterActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingPlayerFilterActionPerformed

    private void KAmplStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_KAmplStateChanged
        p_Ampl = (float)(KAmpl.getValue()/100.);
        FilterDeep.setText(""+KAmpl.getValue());
    }//GEN-LAST:event_KAmplStateChanged

    private void ViewFilteredSpectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewFilteredSpectorActionPerformed
        getMenuState();
    }//GEN-LAST:event_ViewFilteredSpectorActionPerformed

    private void FilterModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_FilterModeItemStateChanged
        p_FilterMode = FilterMode.getSelectedIndex();
    }//GEN-LAST:event_FilterModeItemStateChanged

    private void ViewGammatonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewGammatonActionPerformed
        getMenuState();
    }//GEN-LAST:event_ViewGammatonActionPerformed

    private void GammatoneCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_GammatoneCaretPositionChanged

    }//GEN-LAST:event_GammatoneCaretPositionChanged

    private void GammatoneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_GammatoneStateChanged
        p_GTFNote = Gammatone.getValue();
        NGammatone.setText(fft.getNoteNameByIndex(p_GTFNote));
    }//GEN-LAST:event_GammatoneStateChanged

    private void NGammatoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NGammatoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NGammatoneActionPerformed

    private void ViewCohleogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewCohleogramActionPerformed
        getMenuState();
    }//GEN-LAST:event_ViewCohleogramActionPerformed

    private void NeuronIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NeuronIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NeuronIdActionPerformed

    private void AmplStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AmplStateChanged
        kAmpl = (float)(Ampl.getValue()/500.);
        if (kAmpl>1)
            kAmpl = (float)(Math.exp(kAmpl)/Math.E);
        toLog("KAmpl="+kAmpl);
        fft.setKAmpl(kAmpl);
    }//GEN-LAST:event_AmplStateChanged

    private void SettingGPUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SettingGPUItemStateChanged
        getMenuState();
    }//GEN-LAST:event_SettingGPUItemStateChanged

    private void ViewSoucrceSpectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewSoucrceSpectorActionPerformed
        getMenuState();
    }//GEN-LAST:event_ViewSoucrceSpectorActionPerformed

    private void SettingGPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingGPUActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingGPUActionPerformed

    private void SaveModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveModelActionPerformed
        FileDialog dlg=new FileDialog(this,"Сохранить модель",FileDialog.SAVE);
        dlg.setFile("*.txt");
        dlg.show();
        String ss1 = dlg.getDirectory();
        String ss2 = dlg.getFile();
        StringBuffer buf = new StringBuffer();
        if (ss1==null || ss2==null){
            toLog("Файл не найден");
            return;
            }
        try {
            BufferedWriter is = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ss1+ss2),"Cp1251"));
            I_LayerModel mod = modelFactory.getSelected();
            mod.save(is);
            is.close();
            } catch (Exception ex) {
                toLog(ex);
                }
    }//GEN-LAST:event_SaveModelActionPerformed

    private void SaveXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveXMLActionPerformed
        FileDialog dlg=new FileDialog(this,"Описание модели",FileDialog.LOAD);
        dlg.setFile("*.xml");
        dlg.show();
        String ss1 = dlg.getDirectory();
        String ss2 = dlg.getFile();
        StringBuffer buf = new StringBuffer();
        if (ss1==null || ss2==null){
            toLog("Файл не найден");
            return;
            }
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(ss1+ss2),"Cp1251");
            int cc;
            while((cc=is.read())!=-1){
                buf.append((char)cc);
                }
            is.close();
            ModelCreator creator = new ModelCreator();
            String ss = buf.toString();
            Mes.append(ss);        
            NLM_Constructive res = creator.compile(ss,p_SubToneCount);  
            NLM_Proxy mod = (NLM_Proxy )factory.getByName(GBL.LoadedModel);
            if (mod!=null)
                mod.setOrig(res);
            setStatList();
            } catch (Exception ex) {
                toLog(ex);
                }
    }//GEN-LAST:event_SaveXMLActionPerformed

    private void LoadModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadModelActionPerformed
        FileDialog dlg=new FileDialog(this,"Загрузить модель",FileDialog.LOAD);
        dlg.setFile("*.txt");
        dlg.show();
        String ss1 = dlg.getDirectory();
        String ss2 = dlg.getFile();
        StringBuffer buf = new StringBuffer();
        if (ss1==null || ss2==null){
            toLog("Файл не найден");
            return;
            }
        try {
            BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(ss1+ss2),"Cp1251"));
            factory.load(is);
            is.close();
            } catch (Exception ex) {
                toLog(ex);
                }
    }//GEN-LAST:event_LoadModelActionPerformed

    private void SettingLearningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingLearningActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingLearningActionPerformed

    private void LogLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogLevelItemStateChanged
        p_LogLevel = LogLevel.getSelectedIndex();
    }//GEN-LAST:event_LogLevelItemStateChanged


    private void showStatistic(LayerStatistic stat){
        String sName = stat.getName();
        toLog("Отсчетов:"+stat.getCount());
        float mid = stat.getMid();
        toLog("Среднее:"+mid);
        toLog("Приведенное станд.откл:"+stat.getDisp()/mid);
        toLog("Приведенная неравн.по T:"+stat.getDiffT()/mid);
        toLog("Приведенная неравн.по F:"+stat.getDiffF()/mid);
        int subTOneCount = fft.isLogFreqMode() ? fft.getSubToneCount() : 0;
        if (StatSmooth.isSelected())
            stat.smooth(Integer.parseInt(KSmooth.getText()));
        if (!p_GraphFX){
            int subTone = fft.getSubToneCount();
            FFTLayerWindow ff;
            if (StatMiddle1.isSelected()){
                ff = new FFTLayerWindow(11,FFTView.this,350,sName+" Приведенное станд. отклонение",true,subTone,p_White);
                ff.paint(stat.getDisps(),0);
                }
            if (StatDiff.isSelected()){
                ff = new FFTLayerWindow(10,FFTView.this,350,sName+" Среднее",true,subTone,p_White);
                ff.paint(stat.getMids(),0);
                }
            if (StatDiffT.isSelected()){
                ff = new FFTLayerWindow(12,FFTView.this,350,sName+" Приведенная неравн.по T",true,subTone,p_White);
                ff.paint(stat.getDiffsT(),0);
                }
            if (StatDiffF.isSelected()){
                ff = new FFTLayerWindow(13,FFTView.this,350,sName+" Приведенная неравн.по F",true,subTone,p_White);
                ff.paint(stat.getDiffsF(),0);
                }
            }
        else{
            if (panels[7]==null){
                LineGraphicFrame gg = new LineGraphicFrame(this,fft.getParams());
                gg.setNoFirstLast(Integer.parseInt(NoFirst.getText()),Integer.parseInt(NoLast.getText()));
                gg.setFreq(100);
                panels[7] = gg;
                }
            if (StatMiddle1.isSelected())
                panels[7].paint(stat.getMids(), sName+" Среднее");
            if (StatDiff.isSelected())
                panels[7].paint(stat.getDisps(), sName+" Приведенное станд. отклонение");
            if (StatDiffT.isSelected())
                panels[7].paint(stat.getDiffsT(), sName+" Приведенная неравн.по T");
            if (StatDiffF.isSelected())
                panels[7].paint(stat.getDiffsF(), sName+" Приведенная неравн.по F");
            panels[7].setVisible(true);
            panels[7].repaint();
            }
        }

    private void ShowStatisticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowStatisticActionPerformed
        if (model==null){
            toLog(true,"Нет модели");
            return;
            }
        LayerStatistic stat = statFactory.getSelected();
        if (stat==null){
            toLog(true,"Нет статистики");
            return;
            }
        showStatistic(stat);
    }//GEN-LAST:event_ShowStatisticActionPerformed

    private void ModelIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModelIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ModelIdActionPerformed

    private void StatisticViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StatisticViewActionPerformed
        getMenuState();
    }//GEN-LAST:event_StatisticViewActionPerformed

    private void SettingFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingFCActionPerformed
        getMenuState();
    }//GEN-LAST:event_SettingFCActionPerformed

    private void WhiteBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WhiteBackActionPerformed
        getMenuState();
    }//GEN-LAST:event_WhiteBackActionPerformed

    private void ViewAmplitudesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewAmplitudesActionPerformed
            getMenuState();
    }//GEN-LAST:event_ViewAmplitudesActionPerformed

    private void SettingFFTWindowReduceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingFFTWindowReduceActionPerformed
            getMenuState();
    }//GEN-LAST:event_SettingFFTWindowReduceActionPerformed

    private boolean convertMode=false;
    private Runnable procCohle = new Runnable(){
        public void run(){
            FFTAudioSource src = null;
            src = sourceFactory.getSelected();
            if (src instanceof FFTFileSource){
                FFTFileSource file  = (FFTFileSource)src;
                boolean ff = file.testAndOpenFile(FFTAudioFile.OpenAndPlay,p_lastFileDir+p_lastFileName,FFT.sizeHZ,back);
                if (!ff) return;
                src = file;
                }
            try {
                fft.setFFTParams(new FFTParams(p_BlockSize*FFT.Size0,p_OverProc,
                    p_LogFreq,p_SubToneCount,
                    needCohle(),
                    p_GPU,p_FFTWindowReduce,p_GPUmode,kMultiple));
                if (convertMode)
                    fft.preloadFullCohleogramm(src, back);
                else
                    fft.preloadFullSpectrum(src, back);
                } catch (Exception ex) { 
                    toLog(ex);     
                    }
            }
        };
    
    
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void PreloadSpectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreloadSpectorActionPerformed
        saveCurrentViewState();
        convertMode = false;
        Thread tt = new Thread(procCohle);
        tt.setPriority(Thread.MIN_PRIORITY);
        tt.start();
    }//GEN-LAST:event_PreloadSpectorActionPerformed

    private void PreloadCohleogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreloadCohleogramActionPerformed
        saveCurrentViewState();
        convertMode = true;
        Thread tt = new Thread(procCohle);
        tt.setPriority(Thread.MIN_PRIORITY);
        tt.start();
    }//GEN-LAST:event_PreloadCohleogramActionPerformed

    private void PreloadClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreloadClearActionPerformed
        fft.clearPreload();
    }//GEN-LAST:event_PreloadClearActionPerformed

    private void InfoGPUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InfoGPUActionPerformed
        Mes.append(fft.fullGPUInfo());
    }//GEN-LAST:event_InfoGPUActionPerformed

    private void TextToWaveConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextToWaveConvertActionPerformed
        FFTAudioSource src = sourceFactory.getSelected();
        if (!(src instanceof FFTAudioTextFile)){
            toLog("Выберите текстовый файл");
            return;
            }
        FFTAudioTextFile xx = (FFTAudioTextFile)src;
        xx.setnPoints(nTrendPoints);
        xx.convertToWave(p_lastFileDir+p_lastFileName, back);
    }//GEN-LAST:event_TextToWaveConvertActionPerformed

   public FFTCallBack emptyCallBack = new FFTCallBack() {
        @Override
        public void onStart(float stepMS) { toLog("Начало операции");}
        @Override
        public void onFinish() { toLog("Окончание операции");}
        @Override
        public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft) {
            return true; }
        @Override
        public void onError(Exception ee) { toLog("Ошибка: "+createFatalMessage(ee)); }
        @Override
        public void onMessage(String mes) { toLog(mes); }
        };

    private  I_Notify notify = new I_Notify() {
            @Override
            public void onMessage(String mes) {
                toLog(mes);
            }
            @Override
            public void onError(Exception ee) {
                toLog(createFatalMessage(ee));
            }
        };

    private void ExportBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportBinActionPerformed
        final String fname = getInputFileName("Конвертер в mpx","wav",null);
        if (fname==null) return;
        FFTAudioFile audioFile = new FFTAudioFile();
        if (!audioFile.testAndOpenFile(FFTAudioFile.OpenAndPlay,fname,44100, emptyCallBack))
            return;
        fft.setFFTParams(new FFTParams(p_BlockSize*FFT.Size0,p_OverProc, true,p_SubToneCount,
                true, false,p_FFTWindowReduce,p_GPUmode,kMultiple));
        if (!fft.preloadWave(audioFile,emptyCallBack))
            return;
        fft.preloadFullSpectrum(false,audioFile,emptyCallBack);
        fft.preloadFullCohleogramm(false,audioFile,emptyCallBack);
        new Thread(new Runnable(){
            @Override
            public void run() {
                fft.binSave(fname,notify);
            }
        }).start();

    }//GEN-LAST:event_ExportBinActionPerformed

    private void ExportJSONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportJSONActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ExportJSONActionPerformed

    private void PlayMPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PlayMPXActionPerformed
        String fname = getInputFileName("Играть MPX","mpx",null);
            toLog("Файл: "+fname);
            fft.binLoad(fname,notify);
            p_Play=false;
            runToPlay(new Runnable() {
                @Override
                public void run() {
                    try {
                        fft.fftDirect(back);
                        } catch (Exception e) {
                        toLog(createFatalMessage(e));
                        }
                    }
                });
    }//GEN-LAST:event_PlayMPXActionPerformed

    private void showExtrems(boolean mode){
        int sz = inputStat.getMids().length;
        toLog(String.format("Диапазон экстремумов: %6.4f-%6.4f",50./sz*noFirstPoints,50./sz*(sz-noLastPoints)));
        ArrayList<Extreme> list = inputStat.createExtrems(mode,noFirstPoints,noLastPoints,false);
        if (list.size()==0){
            toLog("Экстремумов не найдено");
            return;
            }
        int nFirst = Integer.parseInt(FirstN.getText());
        int count = nFirst < list.size() ? nFirst : list.size();
        Extreme extreme = list.get(0);
        double val0 = mode ? extreme.value : extreme.diff;
        toLog(mode ? "По амплитуде" : "По спаду");
        toLog(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц",extreme.value,extreme.diff,extreme.freq/KF100));
        double sum=0;
        for(int i=1; i<count;i++){
            extreme = list.get(i);
            double proc = (mode ? extreme.value : extreme.diff)*100/val0;
            sum+=proc;
            toLog(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц %d%%",extreme.value,extreme.diff,extreme.freq/KF100,(int)proc));
            }
        toLog(String.format("Средний - %d%% к первому",(int)(sum/(count-1))));
        }

    private void InputStatisticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputStatisticActionPerformed
        if (RemoveExp.isSelected())
            toLog("Коррекция exp k="+inputStat.correctExp(Integer.parseInt(NPointsExp.getText())));
        showStatistic(inputStat);
        showExtrems(true);
        showExtrems(false);
    }//GEN-LAST:event_InputStatisticActionPerformed


    public String getInputFileName(String title, final String defName, String defDir){
        FileDialog dlg=new FileDialog(this,title,FileDialog.LOAD);
        if (defDir!=null){
            dlg.setDirectory(defDir);
        }
        if (defName.indexOf(".")==-1)
            dlg.setFile("*."+defName);
        else
            dlg.setFile(defName);
        dlg.show();
        String fname=dlg.getDirectory();
        if (fname==null) return null;
        return fname+"/"+dlg.getFile();
        }

    public String getOutputFileName(String title, final String defName, String srcName){
        FileDialog dlg=new FileDialog(this,title,FileDialog.SAVE);
        dlg.setFile(srcName);
        dlg.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("."+defName);
            }
        });
        dlg.show();
        if (dlg.getDirectory()==null) return null;
        String fname = dlg.getFile();
        if (!fname.endsWith("."+defName))
            fname+="."+defName;
        return dlg.getDirectory()+"/"+fname;
        }

    public String createFatalMessage(Throwable ee) {
        return createFatalMessage(ee,6);
    }
    public static String createFatalMessage(Throwable ee, int stackSize) {
        String ss = ee.toString() + "\n";
        StackTraceElement dd[] = ee.getStackTrace();
        for (int i = 0; i < dd.length && i < stackSize; i++) {
            ss += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber() + "\n";
        }
        String out = "Программная ошибка:\n" + ss;
        return out;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FFTView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FFTView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FFTView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FFTView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FFTView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider Ampl;
    private javax.swing.JMenuItem ExportBin;
    private javax.swing.JMenuItem ExportJSON;
    private javax.swing.JButton File;
    private javax.swing.JTextField FileName;
    private javax.swing.JTextField FilterDeep;
    private javax.swing.JComboBox FilterMode;
    private javax.swing.JTextField FirstN;
    private javax.swing.JComboBox<String> GPUmode;
    private javax.swing.JLabel GammaLabel;
    private javax.swing.JSlider Gammatone;
    private javax.swing.JMenu Info;
    private javax.swing.JMenuItem InfoGPU;
    private javax.swing.JButton InputStatistic;
    private javax.swing.JSlider KAmpl;
    private javax.swing.JTextField KCompress;
    private javax.swing.JTextField KMultiple;
    private javax.swing.JTextField KSmooth;
    private javax.swing.JLabel LCompress;
    private javax.swing.JLabel LCompress1;
    private javax.swing.JComboBox LayerId;
    private javax.swing.JMenuItem LoadModel;
    private javax.swing.JComboBox LogLevel;
    private javax.swing.JTextField M1;
    private javax.swing.JTextField M2;
    private javax.swing.JTextField M3;
    private javax.swing.JTextField M4;
    private javax.swing.JTextField M5;
    private javax.swing.JTextField MaxAmpl;
    private java.awt.TextArea Mes;
    private javax.swing.JTextField Mes2;
    private javax.swing.JComboBox ModelId;
    private javax.swing.JComboBox ModelSourceType;
    private javax.swing.JTextField NGammatone;
    private javax.swing.JTextField NNeightbors;
    private javax.swing.JTextField NPointsExp;
    private javax.swing.JTextField NSynapses;
    private javax.swing.JTextField NTrendPoints;
    private javax.swing.JComboBox NeuronId;
    private javax.swing.JTextField NoFirst;
    private javax.swing.JTextField NoLast;
    private javax.swing.JButton Pause;
    private javax.swing.JButton PlayMPX;
    private javax.swing.JMenuItem PreloadClear;
    private javax.swing.JMenuItem PreloadCohleogram;
    private javax.swing.JMenuItem PreloadSpector;
    private javax.swing.JCheckBox RemoveExp;
    private javax.swing.JButton Run;
    private javax.swing.JSlider SCompress;
    private javax.swing.JComboBox Samples;
    private javax.swing.JMenuItem SaveModel;
    private javax.swing.JMenuItem SaveXML;
    private javax.swing.JCheckBoxMenuItem SettingCompress;
    private javax.swing.JCheckBoxMenuItem SettingFC;
    private javax.swing.JCheckBoxMenuItem SettingFFTWindowReduce;
    private javax.swing.JCheckBoxMenuItem SettingGPU;
    private javax.swing.JCheckBoxMenuItem SettingLearning;
    private javax.swing.JCheckBoxMenuItem SettingLogariphmic;
    private javax.swing.JCheckBoxMenuItem SettingPlayer;
    private javax.swing.JCheckBoxMenuItem SettingPlayerFilter;
    private javax.swing.JCheckBoxMenuItem SettingRealTime;
    private javax.swing.JCheckBoxMenuItem SettingRepeat;
    private javax.swing.JMenu Settings;
    private javax.swing.JButton ShowStatistic;
    private javax.swing.JCheckBox StatDiff;
    private javax.swing.JCheckBox StatDiffF;
    private javax.swing.JCheckBox StatDiffT;
    private javax.swing.JCheckBox StatMiddle1;
    private javax.swing.JCheckBox StatSmooth;
    private javax.swing.JCheckBoxMenuItem StatisticView;
    private javax.swing.JComboBox Statistics;
    private javax.swing.JMenuItem TextToWaveConvert;
    private javax.swing.JTextField Time;
    private javax.swing.JTextField ToneCount;
    private javax.swing.JCheckBoxMenuItem ViewAmplitudes;
    private javax.swing.JCheckBoxMenuItem ViewCohleogram;
    private javax.swing.JCheckBoxMenuItem ViewFilteredSpector;
    private javax.swing.JCheckBoxMenuItem ViewGammaton;
    private javax.swing.JCheckBoxMenuItem ViewInputAmplitude;
    private javax.swing.JCheckBoxMenuItem ViewSoucrceSpector;
    private javax.swing.JCheckBoxMenuItem WhiteBack;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JMenu Вид;
    private javax.swing.JMenu Операции;
    // End of variables declaration//GEN-END:variables

}
