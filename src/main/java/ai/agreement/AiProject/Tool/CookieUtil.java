package ai.agreement.AiProject.Tool;

import ai.agreement.AiProject.enums.CookieName;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

    /**
     * <p>checkCookie: 요청에 쿠키가 존재하는지 판단하는 메소드</p>
     * <p>요청에 특정 쿠키가 존재하면 true 를, 특정 쿠키가 존재하지 않으면 false 를 return</p>
     * <p>사용하는 부분에서 if(!CookieUtils.checkCookie(request, cookieName)) 으로</p>
     * <p>쿠키가 존재하지 않을 경우에만 true로 잡아서 처리할 수 있음</p>
     * @param request HttpServletResponse 요청 객체
     * @param customCookie enum CustomCookie 열거형 타입
     * @return boolean
     */
    public static boolean checkCookie(HttpServletRequest request, CookieName customCookie) {

        String cookieName = customCookie.getCookieName(); // 쿠키 이름 획득

        Cookie[] cookies = request.getCookies(); // 쿠키 획득

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * <p>getCookie: 쿠키를 가져오는 메소드, 쿠키가 없으면 null 리턴 </p>
     * @param request 요청 객체
     * @param customCookie enum CustomCookie 열거형 객체
     * @return String cookie.getValue()
     */
    public static String getCookie(HttpServletRequest request, CookieName customCookie) {
        String cookieName = customCookie.getCookieName();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
