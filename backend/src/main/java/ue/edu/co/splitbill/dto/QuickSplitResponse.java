package ue.edu.co.splitbill.dto;

// remainder: pesos que sobran al dividir; los primeros "remainder" pagan 1 peso más
public record QuickSplitResponse(long subtotal, long tip, long total, int people, long perPerson, long remainder) {
}
