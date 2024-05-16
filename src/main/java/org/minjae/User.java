package org.minjae;

public class User {
    private String password;

    public void initPassword(){
        RandomPasswordGenerate generate = new RandomPasswordGenerate();
        String random = generate.generatePassword();

        if(random.length() <= 12 && random.length() >= 8){
            this.password = random;
        }


    }

    public String getPassword() {
        return password;
    }
}
