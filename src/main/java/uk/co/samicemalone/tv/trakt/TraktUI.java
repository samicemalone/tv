/*
 * Copyright (c) 2015, Ice. All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of Sam Malone nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.co.samicemalone.tv.trakt;

import com.uwetrottmann.trakt5.entities.DeviceCode;
import com.uwetrottmann.trakt5.entities.SearchResult;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Ice 
 */
public class TraktUI {
    
    public static void promptForDeviceCodeConfirmation(DeviceCode deviceCode) {
        System.out.format("Please visit the following URL to authorize access to Trakt\n\n  %s\n\n", deviceCode.verification_url);
        System.out.format("Enter the following app authorization code: %s\n\n", deviceCode.user_code);
        System.out.println("Press Enter to when the app has been authorized.");
        try (Scanner s = new Scanner(System.in)) {
            s.nextLine();
        } catch(Exception e) {

        }
    }
    
    /**
     * Reads the users show choice from stdin.
     * @param minValue Minimum value to accept
     * @param maxValue Maximum value to accept
     * @param cancel Value to cancel/abort
     * @return users input between minValue and maxValue or cancel if the user
     * cancelled
     */
    private static int readShowChoiceFromStdin(int minValue, int maxValue, int cancel) {
        System.out.format("Enter the id that matches the show or %d to cancel: \n", cancel);
        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                try {
                    int i = s.nextInt();
                    if(i == cancel) {
                        return cancel;
                    } else if(i >= minValue && i <= maxValue) {
                        return i;
                    }
                } catch (InputMismatchException ex) {}
                System.out.format("Enter a value between %d and %d: \n", minValue, maxValue);
            }
        }
    }
    
        
    /**
     * Display the list of tv show search results with a 1 based index id 
     * displayed with it.
     * @param query search query
     * @param shows list of shows to display
     */
    private static void displayShowSearchResults(String query, List<SearchResult> shows) {
        if(shows.isEmpty()) {
            System.out.println("No results found for " + query);
        } else {
            System.out.println("Search results for " + query);
            for(int i = 0; i < shows.size(); i++) {
                SearchResult result = shows.get(i);
                System.out.format(" %1$2s) [%2$s] %3$s\n", String.valueOf(i+1), result.show.year, result.show.title);
            }
        }
    }

    public static SearchResult readShowSearchResult(String showName, List<SearchResult> shows) {
        if(shows != null && !shows.isEmpty()) {
            TraktUI.displayShowSearchResults(showName, shows);
            int choice = TraktUI.readShowChoiceFromStdin(1, shows.size(), 0);
            if(choice == 0) {
                return null;
            }
            return shows.get(choice - 1);
        }
        return null;
    }
    
}
