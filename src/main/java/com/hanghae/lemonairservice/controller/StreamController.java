package com.hanghae.lemonairservice.controller;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streams")
public class StreamController {
    private final StreamService streamService;


    @PostMapping("/{streamName}/check")
    public ResponseEntity<Boolean> checkStreamValidity(@PathVariable String streamName, @RequestBody StreamKeyRequestDto streamKey) {
        boolean isValid = streamService.checkStreamValidity(streamName, streamKey);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/{streamName}/onair")
    public ResponseEntity<String> startStream(@RequestParam String streamName) {
        boolean started = streamService.startStream(streamName);
        if (started) {
            return ResponseEntity.ok("방송이 시작됩니다. : " + streamName);
        } else {
            return ResponseEntity.badRequest().body("방송 시작을 실패하였습니다. : " + streamName);
        }
    }

//    @PostMapping("/{streamName}/streaming")
//    public ResponseEntity<String> stopStream(@RequestParam String streamName, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        boolean stopped = streamService.stopStream(streamName, userDetails.getUser());
//        if (stopped) {
//            return ResponseEntity.ok("스트리밍이 종료 되었습니다. : " + streamName);
//        } else {
//            return ResponseEntity.badRequest().body("스트리밍 종료가 실패하였습니다. : " + streamName);
//        }
//    }


}
