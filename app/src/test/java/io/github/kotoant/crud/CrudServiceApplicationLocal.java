package io.github.kotoant.crud;

class CrudServiceApplicationLocal {
    public static void main(String[] args) {
        CrudServiceApplication.main(new String[]{"--spring.profiles.active=test"});
    }
}
