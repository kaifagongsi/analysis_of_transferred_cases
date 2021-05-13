package com.kfgs.aotc.pojo.business;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "aoct_detailsofthecase")
@Data
public class DetailsOfTheCase implements Serializable {

	@Id
	@GeneratedValue
	private Integer id;
	//分类员代码
	private Integer classifiersCode;
	//分类员姓名
	private String classifiersName;
	//申请号
	private String caseId;
	//进案日期
	private String inTime;
	//出案日期
	private String outTime;
	//发明名称
	private String caseTitle;
	//案件类型
	private String caseType;
	//案件状态
	private String state;
	//系统粗分结果
	private String simpleCode;
	//IPC主分类号
	private String ipcmi;
	//IPC副分类号
	private String ipcoi;
	//IPC附加分类号
	private String ipca;
	//CCI号
	private String cci;
	//CCA
	private String cca;
	//CSET
	private String cset;
	
}
