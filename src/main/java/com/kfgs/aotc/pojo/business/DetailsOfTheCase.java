package com.kfgs.aotc.pojo.business;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "aotc_detailsofthecase")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsOfTheCase implements Serializable {


	private String id;
	//分类员代码
	private String classifiersCode;
	//分类员姓名
	private String classifiersName;
	//申请号
	@Id
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
