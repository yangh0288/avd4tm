package avd4tm  // <-- 실제 패키지명으로 바꿀 것

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import java.util.*
import kotlin.collections.iterator

/**
 * MainActivity: FlutterActivity 를 상속해서 Flutter와 통신하는 엔트리 포인트 역할.
 * 여기서 MethodChannel 을 생성하고 Flutter 쪽에서 보낸 메서드 요청을 처리한다.
 */
class MainActivity: FlutterActivity() {
    // 채널 이름은 Dart 쪽과 반드시 동일해야 함.
    private val CHANNEL = "screen_time"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // flutterEngine 이 null 일 수 있으므로 안전하게 접근 (여기선 embedding v2 가 기본)
        MethodChannel(flutterEngine?.dartExecutor?.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    // Flutter가 'getTodayUsage' 를 호출하면 아래 블록 실행
                    "getTodayUsage" -> {
                        try {
                            val data = getUsageData(applicationContext)
                            // result.success 에 전달하려면: JSON-serializable 한 형식이어야 함.
                            // (Map<String, Long>, List<Map<...>> 등)
                            result.success(data)
                        } catch (e: Exception) {
                            // 에러 발생 시 PlatformException 으로 전달되지는 않지만,
                            // result.error 를 사용해 Flutter 측에 에러 정보를 보낼 수 있음.
                            result.error("USAGE_ERROR", e.message, null)
                        }
                    }
                    // 사용시간 접근 설정 화면을 열도록 요청받으면 Intent 로 실행
                    "openUsageAccessSettings" -> {
                        openUsageAccessSettings()
                        result.success(null)
                    }
                    else -> result.notImplemented()
                }
            }
    }

    /**
     * 실제 사용시간을 수집하는 함수.
     * 반환형은 Flutter에서 다루기 쉬운 Map<String, Any> 형태로 구성.
     *
     * 주의: 이 호출은 UI 스레드에서 수행되므로 (복잡한 연산이 길면)
     * 실제 환경에서는 비동기(코루틴 등)로 처리하는 것이 바람직함.
     */
    private fun getUsageData(context: Context): Map<String, Any> {
        // UsageStatsManager 시스템 서비스 가져오기
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // 시간 범위: 현재 시각(end)에서 24시간 전(start)까지
        val end = System.currentTimeMillis()
        val start = end - 1000L * 60 * 60 * 24

        // queryUsageStats: 지정한 기간의 사용 기록을 리스트로 반환
        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, start, end
        )

        // 사용시간을 앱 패키지별로 합산하기 위한 Map
        val appTimeMap = HashMap<String, Long>() // packageName -> totalMillis

        for (usage in stats) {
            val pkg = usage.packageName ?: continue
            val totalTime = usage.totalTimeInForeground // 밀리초 단위
            // 기존 값이 있으면 더해서 합산
            val existing = appTimeMap.getOrDefault(pkg, 0L)
            appTimeMap[pkg] = existing + totalTime
        }

        // 앱별 사용시간을 "분" 단위로 변환한 리스트 생성 (Flutter에서 처리하기 편한 구조)
        val appsList = ArrayList<Map<String, Any>>()
        var totalMinutes = 0L

        for ((pkg, millis) in appTimeMap) {
            val minutes = millis / 60000 // 1분 = 60,000밀리초
            totalMinutes += minutes
            val item = mapOf(
                "package" to pkg,
                "minutes" to minutes
            )
            appsList.add(item)
        }

        // 최종 반환 맵: 총합 + 앱 리스트
        val result = mapOf(
            "totalMinutes" to totalMinutes,
            "apps" to appsList
        )

        return result
    }

    /**
     * 사용시간 접근 권한 설정 화면을 여는 유틸.
     * 사용자가 직접 'Usage access' 권한을 켜도록 유도하는 데 사용.
     */
    private fun openUsageAccessSettings() {
        // ACTION_USAGE_ACCESS_SETTINGS: 사용 통계 접근을 허용/해제할 수 있는 시스템 설정 화면
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
