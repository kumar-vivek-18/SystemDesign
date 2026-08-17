/*
https://excalidraw.com/#json=56NUfUlbblnmKgYOkQnfz,q_hEu3__doOXlslooDxThg

Functional Requiremens:-
1. Allow vehicle to park.
2. Allocate the parking slot on the basis of vehicle type.
3. Generate parking ticket and Reserve the parking slot.
4. Generate the bill on the basis of
 exist - entry time.
6. Pay the parking bill.
7. Open the slot.
8. Support multiple spot types
(small_vehicle, medium_vehile, large_vehicle.
9. There can be multiple slots on the floor.
10. There can be multiple floors as well.


Core Entities
1. ParkingLot
2. Floor
3. ParkingSpot
3. Vehicle
4. ParkingTicket
5. Payment
6. Pricing Stratergy


DB Schema
ParkingLot
1. id
2. name

Floor
1. id
2. name
3. parking_lot_id (ref = ParkingLot(id))

ParkingSpot
1. id
2. floor_id (ref = foor(id))
3. spot_type (small, medium, large)
4. current_vehicle (ref=Vehicle(id))

Vehicle
1. id
2. vehicle_number
3. type (small, medium, large)

Parking Ticket
1. id
2. vehicle_id (ref = vehicle(id))
3. spot_id (ref = ParkingSpot(id)
4. entry_time (time)
5. exit_time (nullable
5. payment_status(PAID/UNPAID)
6. payable_amout (default 0)


Api Endpoints
1. isSlotAvailabe() -> return boolean
->check is any slot availabe or not.
2. parkVehicle(vehicleID) -> return (ticketID, vehicleID, spotID, start_time)
-> finds first empty spotID, then generates ticket with start time and mark spot fulfilled
3. createBill(ticketID) -> return (amount)
-> calculates (exit - entry time -> applies payment stratergy).
4. makePayment(ticketID, amount) -> return boolean
-> make payment via different payment mode like upi, cash, card etc.
-> updates payment_status, payable_amount, exit_time of the ticket for reporting purposes.
5. exitVehicle(ticketID) -> return boolean
-> marks spot empty.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Map;

public class ParkingLotDesign {
    public static void main(String[] args) {

        ParkingLot parkingLot = new ParkingLot(1, "Gaur's Mall Parking.");

        parkingLot.addFloor(1, 2,3,0);
        // parkingLot.addFloor(2, 2,4,5);

        Vehicle bike1 = new Vehicle("UP 20 BC 1234",VehicleType.TWO_WHEELER);
        Vehicle car1 = new Vehicle("UP 10 AB 3456", VehicleType.FOUR_WHEELER);
        Vehicle bus1 = new Vehicle("UP 30 PQ 1290", VehicleType.HEAVY_VEHICLE);

        parkingLot.getTicket(bike1, new HourlyPricingStrategy());
        parkingLot.getTicket(car1, new HourlyPricingStrategy());
        parkingLot.getTicket(bus1, new HourlyPricingStrategy());

        Optional<Double> bill1 = parkingLot.getBill(bike1.getVehicleNumber(), LocalDateTime.now());
        Optional<Double> bill2 = parkingLot.getBill(car1.getVehicleNumber(), LocalDateTime.now());
        Optional<Double> bill3 = parkingLot.getBill(bus1.getVehicleNumber(), LocalDateTime.now());

        Optional<Boolean> payment1 = parkingLot.makePayment(bike1.getVehicleNumber(), 100.0, new CashPaymentStrategy());
        Optional<Boolean> payment2 = parkingLot.makePayment(car1.getVehicleNumber(), 200.0, new CashPaymentStrategy());
        Optional<Boolean> payment3 = parkingLot.makePayment(bus1.getVehicleNumber(), 300.0, new CashPaymentStrategy());

        parkingLot.exitVehicle(bike1.getVehicleNumber());
        parkingLot.exitVehicle(car1.getVehicleNumber());
        parkingLot.exitVehicle(bus1.getVehicleNumber());


    }

}

enum PaymentStatus {
    PAID, UNPAID
}

enum VehicleType {
    TWO_WHEELER(20.0), FOUR_WHEELER(40.0), HEAVY_VEHICLE(60.0);

    private final double hourlyRate;

    VehicleType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getRate() {
        return hourlyRate;
    }
}


class ParkingLot {
    private final int parkingLotID;
    private final String parkingLotName;
    private List<Floor> floors;
    private Map<String, ParkingTicket>tickets;
    private List<PaymentStrategy> paymentStrategies;

    public ParkingLot(int parkingLotID, String parkingLotName) {
        this.parkingLotID = parkingLotID;
        this.parkingLotName = parkingLotName;
        this.floors = new ArrayList<>();
        this.tickets = new HashMap<>();
        this.paymentStrategies = new ArrayList<>();
        this.paymentStrategies.add(new CashPaymentStrategy());
        this.paymentStrategies.add(new CardPaymentStrategy());
        this.paymentStrategies.add(new UpiPaymentStrategy());
    }

    public void addFloor(int floorNumber, int twoWheelerSlots, int fourWheelerSlots, int heavyVehicleSlots) {
        floors.add(new Floor(floorNumber, twoWheelerSlots, fourWheelerSlots, heavyVehicleSlots));
    }

    Optional<ParkingTicket> getTicket(Vehicle vehicle, PricingStrategy pricingStrategy) {
        Optional<ParkingSlot> slotOptional = getSlot(vehicle.getVehicleType());
        if (slotOptional.isEmpty()) {
            System.out.println("No slot available for vehicle type: " + vehicle.getVehicleType());
            return Optional.empty();
        }

        ParkingSlot slot = slotOptional.get();
        slot.occupy(vehicle);
        ParkingTicket ticket = new ParkingTicket(vehicle, pricingStrategy, slot);
        tickets.put(ticket.getVehicle().getVehicleNumber(), ticket);
        System.out.println("Ticket generated: " + ticket.getTicketNumbet());
        System.out.println("Slot occupied: " + slot.getSlotID());
        System.out.println("Vehicle: " + vehicle.getVehicleNumber());
        return Optional.of(ticket);
    }

    public Optional<Double> getBill(String vehicleNumber, LocalDateTime exitTime) {
        ParkingTicket ticket = tickets.get(vehicleNumber);
        if (ticket == null) {
            System.out.println("Ticket not found vehicle: " + vehicleNumber);
            return Optional.empty();
        }
        System.out.println("Bill for ticket: " + ticket.getTicketNumbet() + ":->  " + ticket.getBill(exitTime).orElse(0.0));
        return ticket.getBill(exitTime);
    }

    public Optional<Boolean> makePayment(String vehicleNumber, double amount, PaymentStrategy paymentStrategy) {
        ParkingTicket ticket = tickets.get(vehicleNumber);
        if (ticket == null) {
            System.out.println("Ticket not found for vehicle: " + vehicleNumber);
            return Optional.empty();
        }
        boolean isPaid = paymentStrategy.makePayment(amount);
        ticket.setPaymentStatus(isPaid ? PaymentStatus.PAID : PaymentStatus.UNPAID);
        ticket.setExitTime(LocalDateTime.now());
        ticket.setPayableAmount(amount);
        ticket.setPaymentStrategy(paymentStrategy);

        System.out.println("Payment made for ticket number: " + vehicleNumber + " : " + amount);
        return Optional.of(isPaid);
    }

    public void exitVehicle(String vehicleNumber) {
        ParkingTicket ticket = tickets.get(vehicleNumber);
        if (ticket == null) {
            return;
        }
        System.out.println("Vehicle exited: " + vehicleNumber);
        System.out.println("Slot vacated: " + ticket.getParkingSlot().getSlotID());
        ParkingSlot slot = ticket.getParkingSlot();
        if (slot != null) {
            slot.vacate();
        }
        tickets.remove(vehicleNumber);

    }


    public Optional<ParkingSlot> getSlot(VehicleType vehicleType) {
        for (Floor floor : floors) {
            Optional<ParkingSlot> slot = floor.getSlot(vehicleType);
            if (slot.isPresent()) {
                return slot;
            }
        }
        return Optional.empty();
    }
}

class Floor {
    private final int floorNumber;
    private List<ParkingSlot> parkingSlots;
    private int totalSlots;

    public Floor(int floorNumber, int twoWheelerSlots, int fourWheelerSlots, int heavyVehicleSlots) {
        this.floorNumber = floorNumber;
        this.parkingSlots = new ArrayList<>();

        for(int i=0; i<twoWheelerSlots; i++) {
            ParkingSlot slot = new ParkingSlot(String.format("F%d-TW%d", floorNumber, i+1), VehicleType.TWO_WHEELER);
            this.parkingSlots.add(slot);
        }
        for(int i=0; i<fourWheelerSlots; i++) {
            ParkingSlot slot = new ParkingSlot(String.format("F%d-FW%d", floorNumber, i+1), VehicleType.FOUR_WHEELER);
            this.parkingSlots.add(slot);
        }
        for(int i=0; i<heavyVehicleSlots; i++) {
            ParkingSlot slot = new ParkingSlot(String.format("F%d-HV%d", floorNumber, i+1), VehicleType.HEAVY_VEHICLE);
            this.parkingSlots.add(slot);
        }

        this.totalSlots = twoWheelerSlots + fourWheelerSlots + heavyVehicleSlots;
    }


    public Optional<ParkingSlot> getSlot(VehicleType vehicleType) {
        for (ParkingSlot slot : parkingSlots) {
            if (!slot.isOccupied() && slot.getSlotType() == vehicleType) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }
}

class ParkingSlot {
    private final String slotID;
    private final VehicleType slotType;
    private boolean isOccupied;
    private Vehicle vehicle;
    public ParkingSlot(String slotID, VehicleType slotType) {
        this.slotID = slotID;
        this.slotType = slotType;
        this.isOccupied = false;
        this.vehicle = null;
    }

    public String getSlotID() {
        return slotID;
    }

    public VehicleType getSlotType() {
        return slotType;
    }
    public boolean isOccupied() {
        return isOccupied;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void occupy(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.isOccupied = true;
    }

    public void vacate() {
        this.vehicle = null;
        this.isOccupied = false;
    }
}

class Vehicle {
    private String vehicleNumber;
    private VehicleType vehicleType;

    public Vehicle(String vehicleNumber, VehicleType vehicleType)  {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

class ParkingTicket {
    private String ticketNumber;
    private Vehicle vehicle;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double payableAmount;
    private PaymentStatus paymentStatus;
    private PricingStrategy pricingStrategy;
    private PaymentStrategy paymentStrategy;
    private ParkingSlot parkingSlot;

    public ParkingTicket(Vehicle vehicle, PricingStrategy pricingStrategy, ParkingSlot parkingSlot) {
        this.ticketNumber = "TKT-%d"+ Math.random();
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.pricingStrategy = pricingStrategy;
        this.parkingSlot = parkingSlot;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.paymentStrategy = null;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public Optional<Double> getBill(LocalDateTime exitTime) {
        double bill = pricingStrategy.calculateBill(this, exitTime);
        return Optional.of(bill);
    }

    public String getTicketNumbet() {
        return ticketNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

interface PricingStrategy {
    double calculateBill(ParkingTicket ticket, LocalDateTime exitTime);
}

class HourlyPricingStrategy implements PricingStrategy {
    @Override
    public double calculateBill(ParkingTicket ticket, LocalDateTime exitTime) {
        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), exitTime);
        return hours * ticket.getVehicle().getVehicleType().getRate();
    }
}

class DailyPricingStrategy implements PricingStrategy {
    @Override
    public double calculateBill(ParkingTicket ticket, LocalDateTime exitTime) {
        long days = ChronoUnit.DAYS.between(ticket.getEntryTime(), exitTime);
        return days * ticket.getVehicle().getVehicleType().getRate();
    }
}

interface PaymentStrategy {
    boolean makePayment(double amount);
}

class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean makePayment(double amount) {
        System.out.println("Paid " + amount + " using cash.");
        return true;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean makePayment(double amount) {
        System.out.println("Paid " + amount + " using card.");
        return true;
    }
}

class UpiPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean makePayment(double amount) {
        System.out.println("Paid " + amount + " using UPI.");
        return true;
    }
}
