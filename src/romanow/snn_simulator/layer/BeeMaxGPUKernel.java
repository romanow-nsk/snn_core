package romanow.snn_simulator.layer;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * Created by romanow on 12.06.2017.
 */
public class BeeMaxGPUKernel {
    //------------------------------------------------------
    private static float []copy(float[][] src){
        float out[] = new float[src.length * src[0].length];
        for(int i=0,k=0;i<src.length;i++)
            for(int j=0;j<src[0].length;j++,k++)
                out[k]=src[i][j];
        return out;
        }
    private static float [][]copy(float[] src, int rSize){
        float out[][] = new float[rSize][];
        int cSize = src.length/rSize;
        int k=0;
        for (int i=0;i<rSize;i++){
            out[i]=new float[cSize];
            for(int j=0;j<cSize;j++,k++)
                out[i][j]=src[k];
        }
        return out;
        }
    private static Kernel kernel=null;
    public void parStep(NL_BeeMaxGPUBuffered pp){
        //--------------------------------------------------------------------------------
        final float fIdx[] = pp.fIdx.clone();
        final float idx0[] = pp.idx0.clone();
        final float nextIdx[] = pp.nextIdx.clone();
        final float value[] = pp.value.clone();
        final float inData[] = copy(pp.inData);
        final float outCount[] = copy(pp.outCount);
        final int size = pp.src.size();               // ПРЯМО НЕ БЕРЕТ
        final int depth = pp.depth;
        final int dSize = pp.dSize;
        final float dxVal = pp.dxVal;
        final float kVal = pp.kVal;
        final float dUp = pp.dUp;
        final float kBack = pp.kBack;
        //--------------------------------------------------------------------------------
        kernel = new Kernel(){
            @Override
            public void run() {
                int idx = getGlobalId();                 // idx пчелы = номер потока
                for (int ii=0;ii<depth;ii++){
                    int cIdx = (int)fIdx[idx];           // Текущий индекс
                    int left = (int)idx0[idx]-dSize;
                    if (left <0)
                        left = 0;
                    int right = (int)idx0[idx]+dSize;
                    if (right>=size)
                        right = size-1;
                    nextIdx[idx] = fIdx[idx];
                    boolean toUp=false;
                    float delta = inData[ii*size+cIdx]-value[idx];
                    value[idx] += delta * kVal;
                    int cnt=0;
                    for(int i=cIdx+1;i<size-1 && i<cIdx+dUp;i++)
                        if (inData[ii*size+i]>inData[ii*size+cIdx])
                            cnt++;
                    int cnt2=0;
                    for(int i=cIdx-1; i>=0 && i>=cIdx-dUp;i--)
                        if (inData[ii*size+i]>inData[ii*size+cIdx])
                            cnt2++;
                    if (cnt > 3){
                        nextIdx[idx] += dxVal; // Движение в сторону роста сигнала
                        toUp=true;
                    }
                    if (cnt2 > 3){
                        nextIdx[idx] -= dxVal;
                        toUp=true;
                    }
                    if (delta > 0 || toUp){
                    }
                    else{
                        nextIdx[idx] -= (cIdx-idx0[idx])*kBack;
                    }
                    if (nextIdx[idx]<left)
                        nextIdx[idx]=left;
                    if (nextIdx[idx]>right)
                        nextIdx[idx]=right;
                    outCount[ii*size+(int)fIdx[idx]] +=1;
                    fIdx[idx] = nextIdx[idx];
                }
            }
        };
        if (pp.pars.GPUmode()==0)
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        if (pp.pars.GPUmode()==1)
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
        if (pp.pars.GPUmode()==2)
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
        long tt = System.currentTimeMillis();
        kernel.execute(Range.create(idx0.length));      // ПО КОЛИЧЕСТВУ ПЧЕЛ
        System.out.println("gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("conversion time = "+kernel.getConversionTime());
        System.out.println("execution time = "+kernel.getExecutionTime());
        kernel.dispose();
        //------------------- Вернуть --------------------------------------
        pp.fIdx = fIdx.clone();
        pp.idx0 = idx0.clone();
        pp.nextIdx = nextIdx.clone();
        pp.value = value.clone();
        pp.outCount = copy(outCount,pp.outCount.length);
        }
}
