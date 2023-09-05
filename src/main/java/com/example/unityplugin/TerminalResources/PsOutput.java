package com.example.unityplugin.TerminalResources;

public class PsOutput {
    public String USER;
    public int PID;
    public int PPID;
    public int VSZ;
    public int RSS;
    public String WCHAN;
    public int ADDR;
    public String S;
    public String NAME;
    public boolean valid;

    public PsOutput(String psString)
    {
        valid = false;

        boolean nextField = false;
        int currFieldId = 0;
        String currString = "";
        for(char c: psString.toCharArray()) {

            if(c != ' ')
                currString += c;

            if(c == ' ' && !nextField) {
                nextField = true;
                try {
                    switch (currFieldId) {
                        case 0:
                            USER = currString;
                            break;
                        case 1:
                            PID = Integer.parseInt(currString);
                            break;
                        case 2:
                            PPID = Integer.parseInt(currString);
                            break;
                        case 3:
                            VSZ = Integer.parseInt(currString);
                            break;
                        case 4:
                            RSS = Integer.parseInt(currString);
                            break;
                        case 5:
                            WCHAN = currString;
                            break;
                        case 6:
                            ADDR = Integer.parseInt(currString);
                            break;
                        case 7:
                            S = currString;
                            break;
                        default:
                            valid = false;
                            return;
                    }
                } catch(NumberFormatException e)
                {
                    valid = false;
                    return;
                }
                currString = "";
            }
            if(c != ' ' && nextField) {
                nextField = false;
                currFieldId++;
            }
        }
        NAME = currString;
        valid = true;
    }
}
