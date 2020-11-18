/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.util.List;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.OpenCLPlatform;

public class GPU{
    public class GPUParams{ 
        Device.TYPE type;
        long globalMemSize;
        long localMemSize;
        int maxComputeUnits;
        int maxWorkGroupSize;
        int maxWorkItemDimensions; 
        public GPUParams(){
            if (bestDevice==null)
                return;
            type = bestDevice.getType();
            globalMemSize = ((OpenCLDevice)bestDevice).getGlobalMemSize();
            localMemSize = ((OpenCLDevice) bestDevice).getLocalMemSize();
            maxComputeUnits = ((OpenCLDevice) bestDevice).getMaxComputeUnits();
            maxWorkGroupSize = ((OpenCLDevice) bestDevice).getMaxWorkGroupSize();
            maxWorkItemDimensions =  ((OpenCLDevice) bestDevice).getMaxWorkItemDimensions();
            }
        public String toString(){
            if (bestDevice==null)
                return "";
            String ss = "GPU не выбран";   
            ss += "   Type                  : " + bestDevice.getType()+"\n";
            ss += "   GlobalMemSize         : " + ((OpenCLDevice) bestDevice).getGlobalMemSize()+"\n";
            ss += "   LocalMemSize          : " + ((OpenCLDevice) bestDevice).getLocalMemSize()+"\n";
            ss += "   MaxComputeUnits       : " + ((OpenCLDevice) bestDevice).getMaxComputeUnits()+"\n";
            ss += "   MaxWorkGroupSizes     : " + ((OpenCLDevice) bestDevice).getMaxWorkGroupSize()+"\n";
            ss += "   MaxWorkItemDimensions : " + ((OpenCLDevice) bestDevice).getMaxWorkItemDimensions()+"\n";
            return ss;
            }
        }
    Device bestDevice = null;
    public boolean devicePresent(){
        return bestDevice!=null;
        }
    public GPUParams getGPUParams(){
        return new GPUParams();
        }
    public GPU(boolean p_GPU){
        if (!p_GPU){
            bestDevice=null;
            return;
            }
        List<OpenCLPlatform> platforms = OpenCLPlatform.getPlatforms();
        if (platforms.size()==0)
            return;
        bestDevice = OpenCLDevice.best();
        }        
    public Device getGPUDevice(){
        return bestDevice;
        }
    //--------------------------------------------------------------------------
    public void printGPUInfo(){
        System.out.println(fullGPUInfo());
        }
    public String fullGPUInfo(){
        String ss="";
        List<OpenCLPlatform> platforms = OpenCLPlatform.getPlatforms();
        ss+=("Machine contains " + platforms.size() + " OpenCL platforms\n");
        int platformc = 0;
        for (OpenCLPlatform platform : platforms) {
            ss+=("Platform " + platformc + "{\n");
            ss+=("   Name    : \"" + platform.getName() + "\"\n");
            ss+=("   Vendor  : \"" + platform.getVendor() + "\"\n");
            ss+=("   Version : \"" + platform.getVersion() + "\"\n");
            List<OpenCLDevice> devices = platform.getDevices();
            ss+=("   Platform contains " + devices.size() + " OpenCL devices\n");
            int devicec = 0;
            for (OpenCLDevice device : devices) {
                ss+=("   Device " + devicec + "{\n");
                ss+=("       Type                  : " + device.getType())+"\n";
                ss+=("       GlobalMemSize         : " + device.getGlobalMemSize())+"\n";
                ss+=("       LocalMemSize          : " + device.getLocalMemSize())+"\n";
                ss+=("       MaxComputeUnits       : " + device.getMaxComputeUnits())+"\n";
                ss+=("       MaxWorkGroupSizes     : " + device.getMaxWorkGroupSize())+"\n";
                ss+=("       MaxWorkItemDimensions : " + device.getMaxWorkItemDimensions())+"\n";
                ss+=("   }\n");
                devicec++;
                }
            ss+=("}");
            platformc++;
            }
        Device bestDevice = OpenCLDevice.best();
        if (bestDevice == null) {
            ss+=("OpenCLDevice.best() returned null!\n");
            } else {
            ss+=("OpenCLDevice.best() returned {\n");
            ss+=("   Type                  : " + bestDevice.getType())+"\n";
            ss+=("   GlobalMemSize         : " + ((OpenCLDevice) bestDevice).getGlobalMemSize())+"\n";
            ss+=("   LocalMemSize          : " + ((OpenCLDevice) bestDevice).getLocalMemSize())+"\n";
            ss+=("   MaxComputeUnits       : " + ((OpenCLDevice) bestDevice).getMaxComputeUnits())+"\n";
            ss+=("   MaxWorkGroupSizes     : " + ((OpenCLDevice) bestDevice).getMaxWorkGroupSize())+"\n";
            ss+=("   MaxWorkItemDimensions : " + ((OpenCLDevice) bestDevice).getMaxWorkItemDimensions())+"\n";
            ss+=("}\n");
            }
        /*
        Device firstCPU = OpenCLDevice.firstCPU();
        if (firstCPU == null) {
            ss+=("OpenCLDevice.firstCPU() returned null!");
            } else {
            ss+=("OpenCLDevice.firstCPU() returned { ");
            ss+=("   Type                  : " + firstCPU.getType());
            ss+=("   GlobalMemSize         : " + ((OpenCLDevice) firstCPU).getGlobalMemSize());
            ss+=("   LocalMemSize          : " + ((OpenCLDevice) firstCPU).getLocalMemSize());
            ss+=("   MaxComputeUnits       : " + ((OpenCLDevice) firstCPU).getMaxComputeUnits());
            ss+=("   MaxWorkGroupSizes     : " + ((OpenCLDevice) firstCPU).getMaxWorkGroupSize());
            ss+=("   MaxWorkItemDimensions : " + ((OpenCLDevice) firstCPU).getMaxWorkItemDimensions());
            ss+=("}");
            }
        Device firstGPU = OpenCLDevice.firstGPU();
        if (firstGPU == null) {
            ss+=("OpenCLDevice.firstGPU() returned null!");
            } else {
            ss+=("OpenCLDevice.firstGPU() returned { ");
            ss+=("   Type                  : " + firstGPU.getType());
            ss+=("   GlobalMemSize         : " + ((OpenCLDevice) firstGPU).getGlobalMemSize());
            ss+=("   LocalMemSize          : " + ((OpenCLDevice) firstGPU).getLocalMemSize());
            ss+=("   MaxComputeUnits       : " + ((OpenCLDevice) firstGPU).getMaxComputeUnits());
            ss+=("   MaxWorkGroupSizes     : " + ((OpenCLDevice) firstGPU).getMaxWorkGroupSize());
            ss+=("   MaxWorkItemDimensions : " + ((OpenCLDevice) firstGPU).getMaxWorkItemDimensions());
            ss+=("}");
            }
         */    
        return ss;
        }
        
}
