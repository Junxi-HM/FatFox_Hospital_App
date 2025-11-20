package com.example.fatfoxhospital;
import java.util.Arrays;
import java.util.List;
public class NurseList {
    public static final List<Nurse> mockNurses = Arrays.asList(
            new Nurse(1, "Alice", "Johnson", "alice.j", "pass123"),
            new Nurse(2, "Alina", "Kovacs", "alina.k", "qwerty"),
            new Nurse(3, "Bob", "Smith", "bob.s", "abc123"),
            new Nurse(4, "Charlie", "Brown", "charlie.b", "password123"),
            new Nurse(5, "David", "Lee", "david.l", "letmein")
    );
}
