import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        // Load the JSON test case 1
        String content1 = new String(Files.readAllBytes(Paths.get("testcase1.json")));
        String content2 = new String(Files.readAllBytes(Paths.get("testcase2.json")));

        // Parse JSON
        JSONObject json1 = new JSONObject(content1);
        JSONObject json2 = new JSONObject(content2);

        // Solve for both test cases
        BigInteger result1 = solve(json1);
        BigInteger result2 = solve(json2);

        // Output the result
        System.out.println("Constant term (c) for TestCase 1: " + result1);
        System.out.println("Constant term (c) for TestCase 2: " + result2);
    }

    // Function to solve the problem using Lagrange interpolation
    public static BigInteger solve(JSONObject json) {
        int n = json.getJSONObject("keys").getInt("n"); // Number of points
        int k = json.getJSONObject("keys").getInt("k"); // Minimum points required to solve

        // List to store points (x, y)
        List<Point> points = new ArrayList<>();

        // Extract the points from JSON
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key); // x is the key in the JSON object
            JSONObject point = json.getJSONObject(key);
            int base = Integer.parseInt(point.getString("base")); // Get base
            String value = point.getString("value"); // Get encoded value in the base

            // Decode the y-value from the given base
            BigInteger y = new BigInteger(value, base);

            // Store the point (x, y)
            points.add(new Point(x, y));
        }

        // Apply Lagrange Interpolation to find the constant term (c)
        return lagrangeInterpolation(points, k);
    }

    // Lagrange interpolation to find the constant term (c) of the polynomial
    public static BigInteger lagrangeInterpolation(List<Point> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Apply the Lagrange interpolation formula
        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    numerator = numerator.multiply(xj.negate()); // (0 - xj)
                    denominator = denominator.multiply(xi.subtract(xj)); // (xi - xj)
                }
            }

            // Calculate the term L_i(0) * yi and add it to the result
            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    // Helper class to represent a point (x, y)
    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}

