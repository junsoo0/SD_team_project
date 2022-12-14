package reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDateTime;

import account.User;
import file.ReservationFile;
import reservation.mealkit.Mealkit;
import review.Review;

import java.lang.ref.Cleaner;


public class CleaningReservation {
    private String          reserveType = "없음";    // 정기 or 1회 or 없음
    private LocalDateTime   signUpTime;     // 청소 요청 시간
    private int             cleaningSpace = 0;  // 청소 면적
    private int             price = 0;          // 청소 가격만
    private LocalDateTime   reservationDate;// 청소 예정 시간
    private String          location = "없음";       // 청소 장소
    private String          processStatus = "예약 전";  // 진행 상태

    private User                    user ;
    private Cleaner                 cleaner;
    private final ArrayList<Mealkit>         mealkit = new ArrayList<Mealkit>(); // 여러개여서 수정!!!>
    private FinishCleaningInfo      finishCleaningInfo;
    private AdditionalOption        additionalOption;
    private ReCleaningReservation   reCleaningReservation;
    private Review                  review;



    public CleaningReservation(User user) {
        this.user = user;
    }
    public void requestClean() {
        Scanner in = new Scanner(System.in);

        // 예약 유형 선택(예외 구현 완료)
        while(true) {
            System.out.print("예약 유형을 선택해 주세요.(1회, 정기) : ");
            this.reserveType        = in.nextLine();
            if(reserveType.equals("1회") || reserveType.equals("정기")){
                break;
            } else {
                System.out.println("다시 입력해 주세요.");
                System.out.println();
            }
        }

        // 청소 면적 선택(예외 구현 완료)
        while(true) {
            while (true) {
                System.out.print("청소 면적을 선택해 주세요.(숫자로 1~40평 사이값을 입력해 주세요.) : ");
                if (in.hasNextInt())
                    break;
                in.next();
                System.out.println("숫자를 입력해주세요.");
                System.out.println();
            }

            this.cleaningSpace = in.nextInt();
            if(cleaningSpace > 40){
                System.out.println(cleaningSpace + "평으로 잘못 입력 했습니다. 다시 입력해 주세요.");
            } else if(cleaningSpace < 1){
                System.out.println(cleaningSpace + "평으로 잘못 입력 했습니다. 다시 입력해 주세요.");
            }else{
                break;
            }
            System.out.println();
        }
        in.nextLine();

        // 예약 기간 선택(예외 구현 완료)
        while(true) {
            System.out.println("예약 기간을 선택해 주세요.(yyyy-MM-dd HH:mm)");
            System.out.println("한자리 숫자인 경우 앞에 0을 붙여주세요.(ex, 2022-06-06 06:00)");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            System.out.print("예약 기간 입력 : ");
            String tempDate = in.nextLine();

            if (checkDateFormat(tempDate)){
                this.reservationDate = LocalDateTime.parse(tempDate, formatter);
            } else {
                System.out.println("잘못된 날짜 형식입니다. 다시 입력해 주세요.");
                System.out.println();
                continue;
            }

            if (reservationDate.isBefore(LocalDateTime.now())) {
                System.out.println("현재 시간 보다 과거 예약일 입니다. 다시 입력해 주세요.");
                System.out.println();
                continue;
            }

            ReservationFile rsrvFile = new ReservationFile();
            String[] filenames = rsrvFile.readAllFileNames(user);

            boolean check = false;
            for (String filename: filenames) {
                if (tempDate.substring(0, 10).equals(filename.substring(0, 10))) {
                    System.out.println("이미 예약된 시간입니다. 다시 입력해 주세요.");
                    System.out.println();
                    check = true;
                    break;
                }
            }
            if(!check) {
                break;
            }
        }


        // 예약 장소 선택(예외 구현 완료)

        while(true){
            while (true) {
                System.out.print("예약 지역을 선택해 주세요.(1.대구, 2.부산, 3.대전, 4.서울, 5.광주) : ");
                if (in.hasNextInt())
                    break;
                in.next();
                System.out.println("숫자를 입력해주세요.");
                System.out.println();
            }
            int city = in.nextInt();

            switch(city) {
                case 1:
                    this.location = "대구 ";
                    break;
                case 2:
                    this.location = "부산 ";
                    break;
                case 3:
                    this.location = "대전 ";
                    break;
                case 4:
                    this.location = "서울 ";
                    break;
                case 5:
                    this.location = "광주 ";
                    break;
                default:
                    System.out.println("잘못 선택하셨습니다. 다시 입력해 주세요.");
                    System.out.println();
                    continue;
            }
            break;
        }
        in.nextLine();

        System.out.print("예약 주소를 선택해 주세요. : ");
        this.location.concat(in.nextLine());

        //추가 옵션 선택 과정
        this.additionalOption = new AdditionalOption(this);
        this.additionalOption.requestAdditionalOption();

        // 밀키트 주문 과정
        int check = 0;
        while(true){
            if(check == 0)
                System.out.print("밀키트를 주문하시겠습니까?(Y/N) : ");
            else
                System.out.print("밀키트를 추가 주문하시겠습니까?(Y/N) : ");
            String answer = in.next();
            if(answer.equals("Y")) {
                Mealkit tempMealkit = new Mealkit(this);
                tempMealkit.requestMealkit();
                this.mealkit.add(tempMealkit);
            }
            else if(answer.equals("N")) {
                break;
            } else {
                System.out.println("'Y' 또는 'N'을 입력해주세요.");
                System.out.println();
            }
            check = 1;
        }
        //진행 상황
        this.processStatus      = "예약 대기";
        
        //가격 측정
        if(cleaningSpace <= 9){
            this.price += 60000;
        } else if(cleaningSpace < 20) {
            this.price += 70000;
        } else if(cleaningSpace < 30) {
            this.price += 80000;
        }else if(cleaningSpace <= 40) {
            this.price += 90000;
        }

        if(reserveType.equals("정기")) {
            this.price *= 4;
        }

        //예약 시간
        this.signUpTime = LocalDateTime.now();

        ReservationFile rsrvFile = new ReservationFile();
        rsrvFile.writeFile(this);

        System.out.println("--------------------------------------------------");
        System.out.println("              모든 예약이 접수되었습니다.");
        System.out.println("--------------------------------------------------");
    }

    public int sumPrice() {
        Iterator<Mealkit> it = mealkit.iterator();
        int sum = this.price; // 청소 가격

        // + 추가 옵션 가격
        sum += additionalOption.getAdditionalPrice();

        // + 모든 밀키트 가격
        while (it.hasNext()) {
            Mealkit temp = it.next();
            sum += temp.getMealkitPrice();
        }

        this.processStatus      = "결제 대기";

        return sum;
    }

    public void arrangeCleaner(Cleaner cleaner) {
        System.out.println("청소 대행자 배치 완료 했습니다.");
        this.processStatus = "청소 대리인 배치 완료";
    }

    public Boolean requestPayment() {
        String answer;
        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.print("결제를 하시겠습니까?(Y/N) : ");
            answer = sc.next();
            if (answer.equals("Y")) {
                break;
            } else if (answer.equals("N")) {
                ReservationFile rsrvFile = new ReservationFile();
                rsrvFile.removeFile(this);
                return false;
            } else {
                System.out.println("'Y' 또는 'N'을 입력해주세요.");
                System.out.println();
            }
        }
        int[] creditInfo = user.getcreditInfo();
        int allPrice = this.sumPrice();

        System.out.println("총 금액 : " + allPrice);
/*
        if (CardPaymentSystem.makePayment(creditInfo, allPrice)) {
            System.out.println("결제 성공 했습니다.");
            this.processStatus = "결제 성공";
            return true;
        }
        else {
             System.out.println("결제 실패 했습니다.");
             this.processStatus = "결제 성공";
             return false;
        }
*/
        System.out.println("결제 성공 했습니다.");
        this.processStatus = "결제 성공";

        ReservationFile rsrvFile = new ReservationFile();
        rsrvFile.writeFile(this);

        return true;
    }

    public void cancelReservation() {
        System.out.println("예약 취소 했습니다");
        this.processStatus = "예약 취소";

        //값 초기화

    }

    public void completeCleaning() {
        finishCleaningInfo = new FinishCleaningInfo(this);
        LocalDateTime tempFinsihCleanTime = this.reservationDate;
        this.finishCleaningInfo.setFinishCleanTime(tempFinsihCleanTime.plusHours(2));
        System.out.println("청소 완료 했습니다.");
        this.processStatus = "청소 완료";

        ReservationFile rsrvFile = new ReservationFile();
        rsrvFile.writeFile(this);
    }

    //날짜 형식 검사
    public static boolean checkDateFormat(String checkDate) {
        String dateFormat = "yyyy-MM-dd HH:mm";
        if(checkDate.length() < dateFormat.length()) {
            return false;
        }

        if(checkDate.length() > dateFormat.length()) {
            return false;
        }

        SimpleDateFormat dateFormatParser = new SimpleDateFormat(dateFormat); //검증할 날짜 포맷 설정
        dateFormatParser.setLenient(false); //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
        try {
            dateFormatParser.parse(checkDate); //대상 값 포맷에 적용되는지 확인
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public String getReserveType() {
        return reserveType;
    }

    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public LocalDateTime getSignUpTime() {
        return signUpTime;
    }

    public void setSignUpTime(LocalDateTime signUpTime) {
        this.signUpTime = signUpTime;
    }

    public int getCleaningSpace() {
        return cleaningSpace;
    }

    public void setCleaningSpace(int cleaningSpace) {
        this.cleaningSpace = cleaningSpace;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFinishCleaningInfo(FinishCleaningInfo finishCleaningInfo) {
        this.finishCleaningInfo = finishCleaningInfo;
    }

    public FinishCleaningInfo getFinishCleaningInfo() {
        return finishCleaningInfo;
    }

    public User getUser() {
        return user;
    }

    public ReCleaningReservation getReCleaningReservation() {
        return this.reCleaningReservation;
    }

    public void setReCleaningReservation(ReCleaningReservation reCleaningReservation) {
        this.reCleaningReservation = reCleaningReservation;
    }
}
