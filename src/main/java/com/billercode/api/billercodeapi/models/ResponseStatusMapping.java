package com.billercode.api.billercodeapi.models;

import java.util.ArrayList;

public record ResponseStatusMapping(String method, String contentType, ArrayList<String> success,
                                    ArrayList<String> fail, ArrayList<String> special) {
}
