##Project1. MyUtilityProject

* MyUtilityProject는 지금까지 배운 계산기, 단위변환, 웹뷰, 구글맵을 이용한 자기위치를 알려주는 Application이다.

###2017.02.06
    *TabView와 ViewPager를 이용해서 MainActivity 생성.
    *3번째 Tab에 웹뷰 적용.
    *4번째 Tab에 구글맵 적용.

###2017.02.07
    *1번째 Tab에 정규식(String splited[] = value.split("(?<=[*/+-])|(?=[*/+-])");)을 이용한 계산기 적용.
    *2번째 Tab에 네이버 단위변환기를 응용한 단위변환기를 적용.
    *4번째 Tab에  위치 이동할 시 실시간 연동하게할 리스너 'LocationListener locationListener = new LocationListener()'를 이용하여 자기 위치 적용.
                 //정보제공자로 네트워크프로바이더 등록
```
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                3000, // 통지사이의 최소 시간간격 (miliSecond)
                                10, // 통지사이의 최소 변경거리 (m)
                                locationListener);
```
