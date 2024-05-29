package com.websiteshop.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.websiteshop.entity.Account;

@Component
public class FacebookOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    AccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Lấy thông tin cần thiết từ OAuth2User
        String username = oAuth2User.getAttribute("name");
        String password = Long.toHexString(System.currentTimeMillis());

        // Tạo tài khoản mới từ thông tin được truyền vào
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(oAuth2User.getAttribute("email"));
        account.setFullname(oAuth2User.getAttribute("name"));
        account.setAddress(oAuth2User.getAttribute("address")); // thêm địa chỉ
        account.setImage(oAuth2User.getAttribute("picture")); // thêm ảnh
        account.setTelePhone(oAuth2User.getAttribute("phone"));
        // Thêm quyền vào tài khoản
        // Lưu thông tin tài khoản vào trong CSDL
        accountService.save(account);

        // Tạo đối tượng UserDetails để đăng nhập
        UserDetails userDetails = User.withUsername(username).password(password).roles("CUST").build();

        return userDetails;
    }
}
