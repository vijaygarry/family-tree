package com.neasaa.base.app.ops.tools;

import com.neasaa.base.app.utils.PasswordUtil;

public class ResetUserPassword {
  public static void main(String[] args) {
    if (args == null || args.length == 0) {
      System.err.println("Usage: " + ResetUserPassword.class.getName() + "<plainTextPassword>");
      System.exit(1);
    }
    System.out.println(
        "Encrypted pwd for " + args[0] + " is " + PasswordUtil.hashPassword(args[0]));
  }
}
