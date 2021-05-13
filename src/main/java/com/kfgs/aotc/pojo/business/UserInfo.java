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
public class UserInfo implements Serializable {
	// 领域组
	private String fieldGroup;
	// 部
	private String dep1;
	// 室
	private String dep2;
	// 姓名
	private String name;
	// E系统姓名
	private String ename;
	@Id
	@GeneratedValue
	private Integer classifiersCode;
	// 业务类型
	private String businessType;
	// 分类领域（大类）
	private String classificationField;
}
