package site.tteolione.tteolione.common.util;

public class DistanceCalculator {
    private static final int R = 6371000; // 지구의 반지름 (미터)
    private static final double WALKING_SPEED = 5.0; // 도보 속도 (킬로미터/시)

    // 경도와 위도를 기반으로 두 지점 간의 거리를 계산하는 메서드
    public static double calculateWalkingDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return R * c; // 결과를 킬로미터로 반환
    }

    // 도보 거리를 기반으로 도보 시간을 계산하는 메서드
    public static int calculateWalkingTime(double distance) {
        double hours = distance / WALKING_SPEED; // 시간 계산
        return (int) (hours * 60); // 분으로 변환하여 반환
    }

}
