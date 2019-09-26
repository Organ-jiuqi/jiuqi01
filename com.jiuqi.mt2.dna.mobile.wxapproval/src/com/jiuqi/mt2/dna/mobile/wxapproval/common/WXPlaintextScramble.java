package com.jiuqi.mt2.dna.mobile.wxapproval.common;

/**
 * 明文扰码器 降低URL中泄漏出接口的信息
 * @author liuzihao
 */
public class WXPlaintextScramble {
	private String code="";
	private String workitemid="";//32位
	private String result="";//扰码后的结果
	//解扰码
	public WXPlaintextScramble(String result) {
		this.result=result;
		if(result.length()>32){
			int cl=result.length()-32;
			int coff=32/cl;
			StringBuffer cod=new StringBuffer();
			StringBuffer wid=new StringBuffer();
			for(int i=0;i<cl;i++){
				cod.append(result.substring((i+1)*(coff+1)-1, (i+1)*(coff+1)));
				wid.append(result.substring(i*(coff+1), (i+1)*(coff+1)-1));
			}
			wid.append(result.substring(cl*(coff+1), result.length()));
			this.code=cod.toString();
			this.workitemid=wid.toString().toUpperCase();
		}else{
			this.code="";
			this.workitemid=result.toUpperCase();
		}
	}
	//加扰码
	public WXPlaintextScramble(String code, String workitemid) {
		this.code=code;
		this.workitemid=workitemid;
		String lwid=workitemid.toLowerCase();
		StringBuffer result=new StringBuffer();
		if(code.length()==0){
			this.result=lwid;
		}else{
			int coff= lwid.length()/code.length();
			for(int i=0;i<code.length();i++){
				result.append(lwid.substring(i*coff, (i+1)*coff));
				result.append(code.substring(i, i+1));
			}
			result.append(lwid.substring(code.length()*coff, lwid.length()));
			this.result=result.toString();
		}
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getWorkitemid() {
		return workitemid;
	}
	public void setWorkitemid(String workitemid) {
		this.workitemid = workitemid;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
