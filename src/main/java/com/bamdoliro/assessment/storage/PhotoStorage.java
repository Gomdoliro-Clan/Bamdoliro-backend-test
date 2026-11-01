package com.bamdoliro.assessment.storage;

/** 증명사진이 올라가 있는 외부 저장소(S3 등). 호출할 때마다 네트워크 왕복이 발생한다고 가정한다. */
public interface PhotoStorage {

    boolean exists(String key);
}
