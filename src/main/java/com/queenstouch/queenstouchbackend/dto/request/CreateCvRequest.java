package com.queenstouch.queenstouchbackend.dto.request;

import com.queenstouch.queenstouchbackend.model.enums.CvType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCvRequest extends UpdateCvRequest {
    private CvType cvType;
}
