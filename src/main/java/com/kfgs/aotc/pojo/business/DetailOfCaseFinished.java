package com.kfgs.aotc.pojo.business;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@Setter
@Getter
@ToString
public class DetailOfCaseFinished implements Serializable {

    private static final long serialVersionUID = 294655766971777057L;
    //案件id
    @Id
    private String id;
    //处理转案分类员id
    private String classifiersCode;
    //IPC主分类号
    private String ipcmi;
    //IPC副分类号
    private String ipcoi;
    //IPC附加分类号
    private String ipca;

    public DetailOfCaseFinished (String id,String classifiersCode,String ipcmi,String ipcoi,String ipca){
        super();
        this.id = id;
        this.classifiersCode = classifiersCode;
        this.ipcmi = ipcmi;
        this.ipcoi = ipcoi;
        this.ipca = ipca;
    }

}
