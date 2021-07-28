package com.kfgs.aotc.pojo.business.ext;

import com.kfgs.aotc.pojo.business.DetailsOfTheCase;
import lombok.Data;

import java.io.Serializable;

//@Entity
@Data
public class DetailsOfTheCaseExt extends DetailsOfTheCase implements Serializable {
    // ipcmi + ipcoi + ipca
    private String ipc;
    // 接受转案的人员 对应的是aotc_transfer_process 中的 RECEIVE_ID
    private String receiveId;
    //
    private String caseId;

    public DetailsOfTheCaseExt(String ipc, String receiveId,String caseId) {
        this.ipc = ipc;
        this.receiveId = receiveId;
        this.caseId = caseId;
    }
}
