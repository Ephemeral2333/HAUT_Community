package com.liyh.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditResult implements Serializable {

    public enum Verdict {
        PASS, SUSPECT, REJECT
    }

    private Verdict verdict;
    private String reason;
    private String category;
}
