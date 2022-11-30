import account.*;
import reservation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int[] tempCreditInfo = {1,2,3};
        User user = new User("usr1", "01034235643", "kynpook@knu.ac.kr", "1234!!", "경북대 북문", tempCreditInfo,false);
        CleaningReservation cr = new CleaningReservation();
        cr.setUser(user);

        String path = System.getProperty("user.dir") + File.separator + user.getname();
        System.out.println("파일 절대 경로 : "+path);
        File file = new File(path);
        file.mkdir(); //user name으로 된 directory 생성, (user's dm layer..)

        System.out.println("--------------------------------------------------");
        System.out.println("                      혼자야?");
        System.out.println("       (1인 가구 청소 대행 및 밀키트 배달 서비스)");
        System.out.println("--------------------------------------------------");

        Scanner in = new Scanner(System.in);

        while(true) {
            System.out.println(user.getname() + "님 반갑습니다.");
            System.out.println("메뉴를 선택해주세요(숫자)\n");
            System.out.println("1. 청소 요청 정보 불러오기");
            System.out.println("2. 청소 요청");
            System.out.println("3. 청소 재요청");
            System.out.println("4. 종료");
            int input = in.nextInt();

            if(input == 1) {
                System.out.println("--------------------------------------------------");
                System.out.println("[" + user.getname() + "님의 예약 정보]");
                String[] filenames = file.list();
                System.out.println("       예약 날짜       진행 상태 ");
                try {
                    int i = 1;
                    for (String filename : filenames) {

                        File rf = new File(path + "/" + filename);
                        BufferedReader reader = new BufferedReader(new FileReader(rf));
                        String sLine = null;
                        System.out.print(i++ + ": ");
                        while( (sLine = reader.readLine()) != null ){
                            System.out.print(sLine + " | ");
                        }

                        System.out.print("\n");
                    }
                }
                catch (NullPointerException e) {
                    System.out.println("요청 정보가 존재하지 않습니다.");
                    continue;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("--------------------------------------------------");
            } else if(input == 2) {
                System.out.println("--------------------------------------------------");
                cr.requestClean();
                File file2 = new File(path + "/" + cr.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+".txt");
                try{
                    if(file2.createNewFile()){
                        System.out.println("File created");
                    }
                    else{
                        System.out.println("File already Exists");
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                System.out.println(cr.getProcessStatus());
                cr.requestPayment();
                cr.completeCleaning();

                try {

                    BufferedWriter writer = new BufferedWriter(new FileWriter(file2));

                    writer.write(cr.getProcessStatus());
                    writer.write("\r\n");
                    writer.write(cr.getFinishCleaningInfo().getFinishCleanTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM")));
                    writer.write("\r\n");
                    writer.write(cr.getProcessStatus());

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("--------------------------------------------------");
            } else if(input == 3) {
                String status;
                String finishCleanTime;


                CleaningReservation cleaningReserv = new CleaningReservation();
                FinishCleaningInfo cleaningInf = new FinishCleaningInfo();

                //파일 불러서 status , finishcleantime string으로 받았다면, for문으로 받았을 때
                //while();{
                HashMap<String, String> cleanInfo = new HashMap<String, String>();
                if (status.equals("청소 완료") && finishCleanTime.plusHours(12).compareTo(LocalDateTime.now()) > 0)
                    cleanInfo.put(finishCleanTime, status);

                for(String key : cleanInfo.keySet()){
                    System.out.println("완료시간 : "+key + "청소상태 : " + cleanInfo.get(key));
                }
                System.out.print("재청소 원하는 날짜 입력 : ");
                finishCleanTime = in.nextLine();

                cleaningReserv.setProcessStatus(cleanInfo.get(finishCleanTime));
                cleaningReserv.setFinishCleaningInfo(cleaningInf);

                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                cleaningInf.setFinishCleanTime(finishCleanTime);

                ReCleaningReservation reCleaningReserv = new ReCleaningReservation(cleaningReserv);
                reCleaningReserv.reRequestClean();


                System.out.println("--------------------------------------------------");
                System.out.println("구현 필요");
                System.out.println("--------------------------------------------------");
            } else if (input == 4){
                System.out.println("감사합니다. 안녕히 가세요.");
                break;
            } else {
                System.out.println("--------------------------------------------------");
                System.out.println("숫자 1 ~ 4를 입력해주세요.");
                System.out.println("--------------------------------------------------");
            }
            System.out.println("\n");
        }


    }

}