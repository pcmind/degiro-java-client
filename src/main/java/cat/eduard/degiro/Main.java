/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.eduard.degiro;

/**
 *
 * @author casa
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DCredentials creds = new DCredentials() {
            @Override
            public String getUsername() {
                return "";
            }

            @Override
            public String getPassword() {
                return "";
            }
        };

        DManager degiro = new DManager(creds);
        degiro.getPortfolio();
    }

}
