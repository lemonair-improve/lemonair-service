package com.hanghae.lemonairservice.service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class StreamService {

    public boolean checkStreamValidity(String streamName, StreamKeyRequestDto streamKey) {
        return false;
    }

    public boolean startStream(String streamName) {
        return false;
    }

    public boolean stopStream(String streamName, Member user) {
        String streamKey = user.getStreamKey();
        return false;
    }
}
