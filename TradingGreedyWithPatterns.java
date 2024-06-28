import java.util.*;

class TradingState {
    int day;
    double stockPrice;
    double cash;
    int stocksHeld;

    TradingState(int day, double stockPrice, double cash, int stocksHeld) {
        this.day = day;
        this.stockPrice = stockPrice;
        this.cash = cash;
        this.stocksHeld = stocksHeld;
    }

    @Override
    public String toString() {
        return "Day " + day + ": Price " + stockPrice + ", Cash " + cash + ", Stocks " + stocksHeld;
    }
}

public class TradingGreedyWithPatterns {
    public static void main(String[] args) {
        // Example stock price data (Open, High, Low, Close)
        double[][] data = {
            {102.03, 106.32, 101.52, 103.79}, // May 23, 2024
            {104.45, 106.47, 103.00, 106.46}, // May 24, 2024
            {110.24, 114.94, 109.88, 113.89}, // May 28, 2024
            {113.05, 115.49, 110.90, 114.82}, // May 29, 2024
            {114.65, 115.82, 109.66, 110.49}, // May 30, 2024
            {112.52, 112.72, 106.94, 109.62}, // May 31, 2024
            {113.62, 115.00, 112.00, 114.99}, // June 3, 2024
            {115.72, 116.60, 114.04, 116.43}, // June 4, 2024
            {118.37, 122.45, 117.47, 122.43}, // June 5, 2024
            {124.05, 125.59, 118.32, 120.99}  // June 6, 2024
        };

        TradingState initialState = new TradingState(0, data[0][3], 1000, 0);
        TradingState result = greedyTradingWithPatterns(data, initialState);
        System.out.println("Final state: " + result);
        System.out.println("Total money: " + (result.cash + result.stocksHeld * data[data.length - 1][3]));
    }

    public static TradingState greedyTradingWithPatterns(double[][] data, TradingState initialState) {
        TradingState currentState = initialState;

        // Define costs for each pattern
        Map<String, Integer> patternCosts = new HashMap<>();
        patternCosts.put("Bullish Engulfing", -1); // Lower cost is better (buy signal)
        patternCosts.put("Bearish Engulfing", 1);  // Higher cost is worse (sell signal)
        patternCosts.put("Hammer", -1);            // Lower cost is better (buy signal)
        patternCosts.put("Shooting Star", 1);      // Higher cost is worse (sell signal)
        patternCosts.put("Doji", 0);               // Neutral (hold signal)
        patternCosts.put("Morning Star", -1);      // Lower cost is better (buy signal)
        patternCosts.put("None", 0);               // Neutral (hold signal)

        for (int day = 1; day < data.length; day++) {
            double[] currentDayData = data[day];
            double[] previousDayData = data[day - 1];
            String pattern = identifyPattern(previousDayData, currentDayData, day);

            // Decision making based on pattern costs
            int cost = patternCosts.get(pattern);

            if (cost < 0 && currentState.cash >= currentDayData[3]) {
                // Buy if the pattern indicates a buying opportunity
                currentState = new TradingState(day, currentDayData[3], currentState.cash - currentDayData[3], currentState.stocksHeld + 1);
                System.out.println("Day " + day + ": Buy at " + currentDayData[3] + " due to " + pattern);
            } else if (cost > 0 && currentState.stocksHeld > 0) {
                // Sell if the pattern indicates a selling opportunity
                currentState = new TradingState(day, currentDayData[3], currentState.cash + currentDayData[3], currentState.stocksHeld - 1);
                System.out.println("Day " + day + ": Sell at " + currentDayData[3] + " due to " + pattern);
            } else {
                // Hold if the pattern is neutral or if no action can be taken
                currentState = new TradingState(day, currentDayData[3], currentState.cash, currentState.stocksHeld);
                System.out.println("Day " + day + ": Hold at " + currentDayData[3]);
            }
        }

        return currentState;
    }

    public static String identifyPattern(double[] previousDay, double[] currentDay, int day) {
        double prevOpen = previousDay[0], prevClose = previousDay[3];
        double curOpen = currentDay[0], curClose = currentDay[3];
        double curHigh = currentDay[1], curLow = currentDay[2];

        if (curOpen < prevClose && curClose > prevOpen && curClose > curOpen) {
            return "Bullish Engulfing";
        } else if (curOpen > prevClose && curClose < prevOpen && curClose < curOpen) {
            return "Bearish Engulfing";
        } else if (curClose > curOpen && (curClose - curOpen) / (curHigh - curLow) > 0.6) {
            return "Hammer";
        } else if (curClose < curOpen && (curOpen - curClose) / (curHigh - curLow) > 0.6) {
            return "Shooting Star";
        } else if (Math.abs(curOpen - curClose) / (curHigh - curLow) < 0.1) {
            return "Doji";
        } else if (day >= 2 && curOpen > previousDay[0] && curClose < prevOpen) {
            return "Morning Star";
        } else {
            return "None";
        }
    }
}

