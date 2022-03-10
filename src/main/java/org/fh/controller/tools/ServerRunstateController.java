package org.fh.controller.tools;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fh.controller.base.BaseController;
import org.fh.entity.PageData;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：服务器信息监控
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/serverRunstate")
public class ServerRunstateController extends BaseController {
	
	/**获取常量数据
	 * @return 
	 */
	@RequestMapping(value="/getData")
	@ResponseBody
	public Object getData() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		Sigar sigar = new Sigar();
		String errInfo = "success";
		PageData pd = new PageData();
		property(pd);					//基本信息
		gethardDiskData(pd,sigar);		//硬盘使用情况
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**获取实时数据
	 * @return 
	 */
	@RequestMapping(value="/realTimeData")
	@ResponseBody
	public Object realTimeData() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		Sigar sigar = new Sigar();
		String errInfo = "success";
		PageData pd = new PageData();
		getJvmMemoryData(pd);			//JVM内存
		getServerMemoryData(pd,sigar);	//服务器内存
		getCpuData(pd,sigar);			//CPU
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**获取网速
	 * @return 
	 */
	@RequestMapping(value="/networkspeed")
	@ResponseBody
	public Object networkspeed() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		Sigar sigar = new Sigar();
		String errInfo = "success";
		PageData pd = new PageData();
		long[] fhbytes1 = getNet(sigar);
		Thread.sleep(1000);
		long[] fhbytes2 = getNet(sigar);
		long rxspeed = fhbytes2[0] - fhbytes1[0];
		long txspeed = fhbytes2[1] - fhbytes1[1];
		pd.put("rxspeed", (double) Math.round((rxspeed/2014.0) * 100) / 100);		//下载网速
		pd.put("txspeed", (double) Math.round((txspeed/1024.0) * 100) / 100);		//上传网速
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**获取JVM内存数据
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	private PageData getJvmMemoryData(PageData pd){
		double byteToMb = 1024.0 * 1024.0;
		double jvmTotal = 0;
		double jvmFree = 0;
		double jvmUse = 0;
		Runtime rt = Runtime.getRuntime();
		jvmTotal = rt.totalMemory() / byteToMb;
		jvmFree = rt.freeMemory() / byteToMb;
		jvmUse = jvmTotal - jvmFree;
		pd.put("totalJvmMemory", (double) Math.round(jvmTotal * 100) / 100);		//JVM总内存空间
		pd.put("useJvmMemory", (double) Math.round(jvmUse * 100) / 100);			//JVM已使用的内存
		return pd;
	}
	
	/**获取服务器内存数据
	 * @param pd
	 * @param sigar
	 * @throws Exception
	 */
	private void getServerMemoryData(PageData pd, Sigar sigar) throws Exception {
		double byteToGb = 1024.0 * 1024.0 * 1024.0;
		double serveotal = 0;
		double serverUse = 0;
		Mem mem = sigar.getMem();
		serveotal = mem.getTotal() / byteToGb;
		serverUse = mem.getUsed() / byteToGb;
		pd.put("totalServerMemory", (double) Math.round(serveotal * 100) / 100);		//服务器总内存空间
		pd.put("useServerMemory", (double) Math.round(serverUse * 100) / 100);			//服务器已使用的内存
	}
	
	/**获取服务器CUP使用率
	 * @param pd
	 * @param sigar
	 * @throws Exception
	 */
	private void getCpuData(PageData pd, Sigar sigar) throws Exception {
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = null;
		cpuList = sigar.getCpuPercList();
		double cpuuse = 0;
		for (int i = 0; i < infos.length; i++) {				//多块或者多核心CPU
			cpuuse += cpuList[i].getCombined() * 100.00;
		}
		pd.put("cpuuse", (double) Math.round(((cpuuse/(infos.length * 100))*100) * 100) / 100 );//使用率
	}
	
	/**获取服务器硬盘情况
	 * @param pd
	 * @param sigar
	 * @throws Exception
	 */
	private void gethardDiskData(PageData pd, Sigar sigar) throws Exception {
		double byteToGb = 1024.0 * 1024.0;
		long totalSize = 0;
		long useSize = 0;
		FileSystem fslist[] = sigar.getFileSystemList();
		for (int i = 0; i < fslist.length; i++) {
			FileSystem fs = fslist[i];
			FileSystemUsage usage = null;
			usage = sigar.getFileSystemUsage(fs.getDirName());
			switch (fs.getType()) {
			case 0: // TYPE_UNKNOWN ：未知
				break;
			case 1: // TYPE_NONE
				break;
			case 2: // TYPE_LOCAL_DISK : 本地硬盘
				totalSize += usage.getTotal();	//总大小
				useSize += usage.getUsed();		//已经使用量
				break;
			case 3:// TYPE_NETWORK ：网络
				break;
			case 4:// TYPE_RAM_DISK ：闪存
				break;
			case 5:// TYPE_CDROM ：光驱
				break;
			case 6:// TYPE_SWAP ：页面交换
				break;
			}
		}
		pd.put("totalDiskSize",(double) Math.round((totalSize / byteToGb) * 10) / 10);	//硬盘总大小
		pd.put("useDiskSize",(double) Math.round((useSize / byteToGb) * 10) / 10);		//硬盘已使用大小
	}
	
	/**获取基础信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	private PageData property(PageData pd) throws Exception {
		Runtime r = Runtime.getRuntime();
		Properties props = System.getProperties();
		InetAddress addr;
		addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress();
		Map<String, String> map = System.getenv();
		String userName = map.get("USERNAME");// 获取用户名
		pd.put("userName", userName);			//计算机用户
		pd.put("IP", ip);						//本地ip地址
		pd.put("HostName", addr.getHostName());	//本地主机名
		pd.put("availableProcessors", r.availableProcessors());		//JVM可以使用的处理器个数
		pd.put("javaversion", props.getProperty("java.version"));	//Java的运行环境版本
		pd.put("javahome", props.getProperty("java.home"));			//Java的安装路径
		pd.put("javavmversion", props.getProperty("java.vm.specification.version"));	//Java的虚拟机规范版本
		pd.put("javavmname", props.getProperty("java.vm.name"));				//Java的虚拟机实现名称
		pd.put("javaclassversion", props.getProperty("java.class.version"));	//Java的类格式版本号
		pd.put("osarch", props.getProperty("os.arch"));		//操作系统的构架
		pd.put("userdir", props.getProperty("user.dir"));	//用户的当前工作目录
		return pd;
	}

	/**获取接收字节数
	 * @return
	 * @throws Exception
	 */
	private static long[] getNet(Sigar sigar) throws Exception {
		long[] fhbytes = new long[2];
		String ifNames[] = sigar.getNetInterfaceList();
		for (int i = 0; i < ifNames.length; i++) {
			String name = ifNames[i];
			NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
			if ((ifconfig.getFlags() & 1L) <= 0L) {
				continue;
			}
			NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
			long fhRxBytes = ifstat.getRxBytes();
			long fhTxBytes = ifstat.getTxBytes();
			if(fhRxBytes > 0) {
				fhbytes[0] = fhRxBytes;
				fhbytes[1] = fhTxBytes;
				return fhbytes;
			}
		}
		fhbytes[0] = 0;
		fhbytes[1] = 0;
		return fhbytes;
	}
	
}