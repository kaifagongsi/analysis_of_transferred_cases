package com.kfgs.aotc.pojo.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "aotc_transfer_process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferProcess implements Serializable {

	@Id
	private String id;
	//申请号
	private String caseId;
	//发送分类员代码
	private Integer sendId;
	//发送分类员姓名
	private String sendName;
	//发送转案日期
	private String  sendTime;
	//接收分类员代码
	private Integer receiveId;
	//接收分类员姓名
	private String receiveName;
	//处理转案日期
	private String receiveTime;
	//提示标题
	private String tipsTitle;
	//提示内容
	private String tipsContent;
	//提示状态
	private String tipsState;
	
}
