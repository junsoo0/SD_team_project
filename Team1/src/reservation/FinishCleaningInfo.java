package reservation;

import java.time.LocalDateTime;
import java.util.Scanner;

public class FinishCleaningInfo {
    private LocalDateTime finishCleanTime;
    private String finishPhoto;

    public void finishClean() {
        Scanner sc = new Scanner(System.in);
        int year, month, day, hour, minute;
        LocalDateTime finishCleanTime;
        String finishPhoto;

        System.out.println("청소 완료 시간 입력");
        System.out.print("연(year): ");
        year = sc.nextInt();
        System.out.print("월(month): ");
        month = sc.nextInt();
        System.out.print("일(day): ");
        day = sc.nextInt();
        System.out.print("시(hour): ");
        hour = sc.nextInt();
        System.out.print("분(minute): ");
        minute = sc.nextInt();

        finishCleanTime = LocalDateTime.of(year, month, day, hour, minute);
        setFinishCleanTime(finishCleanTime);

        System.out.print("청소 완료 사진 경로 입력: ");
        finishPhoto = sc.next();
        setFinishPhoto(finishPhoto);
    }

    public void setFinishCleanTime(LocalDateTime finishCleanTime) {
        this.finishCleanTime = finishCleanTime;
    }

    public void setFinishPhoto(String finishPhoto) {
        this.finishPhoto = finishPhoto;
    }

    public LocalDateTime getFinishCleanTime() {
        return finishCleanTime;
    }

    public String getFinishPhoto() {
        return finishPhoto;
    }
}
