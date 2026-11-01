package com.bamdoliro.assessment.part1;

import com.bamdoliro.assessment.common.ApiResponse;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.support.TestFixtures;
import com.bamdoliro.assessment.web.ApplicationView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 문제 3. 잘못된 전형 값 */
class TrackValidationTest {

    private final TestFixtures f = new TestFixtures();

    private static final List<String> INVALID_TRACKS = Arrays.asList("ROBOTICS", "general", "", "   ", null);

    @Test
    @DisplayName("올바른 전형이면 임시저장이 201 로 생성된다")
    void createWithValidTrack() {
        ApiResponse<ApplicationView> response = f.controller.createDraft(1L, "GENERAL", "photo/1");

        assertEquals(201, response.status());
        assertEquals(Track.GENERAL, response.data().track());
    }

    @Test
    @DisplayName("잘못된 전형이면 임시저장 생성이 400 INVALID_TRACK")
    void createWithInvalidTrack() {
        for (String invalid : INVALID_TRACKS) {
            ApiResponse<ApplicationView> response = f.controller.createDraft(1L, invalid, "photo/1");

            assertEquals(400, response.status(), "입력값: " + invalid);
            assertEquals("INVALID_TRACK", response.code(), "입력값: " + invalid);
        }
    }

    @Test
    @DisplayName("잘못된 전형이면 수정도 400 INVALID_TRACK 이고 원서는 바뀌지 않는다")
    void updateWithInvalidTrack() {
        Application app = f.draft(Track.GENERAL);

        for (String invalid : INVALID_TRACKS) {
            ApiResponse<ApplicationView> response = f.controller.update(app.getId(), invalid, "photo/other");

            assertEquals(400, response.status(), "입력값: " + invalid);
            assertEquals("INVALID_TRACK", response.code(), "입력값: " + invalid);
        }
        assertEquals(Track.GENERAL, f.reload(app).getTrack());
    }

    @Test
    @DisplayName("존재하지 않는 원서를 수정하면 404")
    void updateNotFound() {
        ApiResponse<ApplicationView> response = f.controller.update(9999L, "GENERAL", "photo/1");

        assertEquals(404, response.status());
        assertEquals("APPLICATION_NOT_FOUND", response.code());
    }
}
