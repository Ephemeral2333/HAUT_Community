package com.liyh.model.vo.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel("RAG 智能问答请求")
public class RagAskRequest {

    @NotBlank(message = "问题不能为空")
    @Size(max = 500, message = "问题长度不能超过500字")
    @ApiModelProperty(value = "用户问题", required = true)
    private String question;

    @Min(value = 1, message = "topK 最小为 1")
    @Max(value = 10, message = "topK 最大为 10")
    @ApiModelProperty(value = "检索帖子数量", example = "5")
    private Integer topK = 5;
}
