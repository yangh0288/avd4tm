import 'package:flutter/services.dart';

class ScreenTimeService {
  // Flutter와 네이티브(Android, iOS)를 연결하는 채널 이름 정의
  static const platform = MethodChannel('screen_time');

  /// 오늘 하루 앱 사용시간을 가져오는 메서드
  static Future<Map<String, dynamic>> getTodayUsage() async {
    try {
      final result = await platform.invokeMethod('getTodayUsage');
      // 네이티브에서 JSON 형태로 데이터를 전달받으면 Map으로 변환
      return Map<String, dynamic>.from(result);
    } catch (e) {
      print("Error fetching screen time: $e");
      return {};
    }
  }
}
