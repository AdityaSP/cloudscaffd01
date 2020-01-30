import org.apache.commons.lang.RandomStringUtils

String newOrgId = RandomStringUtils.random(6, "abcdefghijklmnopqrstuvwyz1234567890_".toCharArray());
context.newOrgId = newOrgId