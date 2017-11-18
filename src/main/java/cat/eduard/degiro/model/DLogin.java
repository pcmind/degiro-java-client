/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.eduard.degiro.model;

/**
 *
 * @author casa
 */
public class DLogin {

    private String username;
    private String password;
    private boolean isRedirectToMobile;
    private String loginButtonUniversal;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsRedirectToMobile() {
        return isRedirectToMobile;
    }

    public void setIsRedirectToMobile(boolean isRedirectToMobile) {
        this.isRedirectToMobile = isRedirectToMobile;
    }

    public String getLoginButtonUniversal() {
        return loginButtonUniversal;
    }

    public void setLoginButtonUniversal(String loginButtonUniversal) {
        this.loginButtonUniversal = loginButtonUniversal;
    }

}
