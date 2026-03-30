package com.queenstouch.queenstouchbackend.dto.request;

import com.queenstouch.queenstouchbackend.model.enums.CvType;
import lombok.Data;

@Data
public class CreateCvRequest {
    private String title;
    private CvType cvType;
    private boolean scholarshipMode;
}
