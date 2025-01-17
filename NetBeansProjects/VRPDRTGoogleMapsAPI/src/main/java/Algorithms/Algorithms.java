package Algorithms;

import static Algorithms.Methods.*;
import java.util.*;
import ProblemRepresentation.*;
import java.io.*;
import static Algorithms.Methods.*;
import java.time.Clock;
import VRPDRT.VRPDRT;
import java.util.stream.Collectors;

public class Algorithms {

    private static InstanceData data;
    private String instanceName = "r050n12tw10";
    private String nodesData = "bh_n12s";
    private String adjacenciesData = "bh_adj_n12s";

    public Algorithms() {
        this.data = new InstanceData(instanceName, nodesData, adjacenciesData);
        this.data.readProblemData();
    }

    public Algorithms(String instanceName, String nodesData, String adjacenciesData) {
        this.instanceName = instanceName;
        this.nodesData = nodesData;
        this.adjacenciesData = adjacenciesData;
        this.data = new InstanceData(this.instanceName, this.nodesData, this.adjacenciesData);
        this.data.readProblemData();
    }

    public static InstanceData getData() {
        return data;
    }

    public static void floydWarshall(List<List<Integer>> c, List<List<Integer>> d, Integer n) {
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (c.get(i).get(j) > c.get(i).get(k) + c.get(k).get(j)) {
                        c.get(i).set(j, c.get(i).get(k) + c.get(k).get(j));
                        d.get(i).set(j, d.get(i).get(k) + d.get(k).get(j));
                    }
                }
            }
        }
    }

    public static double FuncaoDeAvaliacao(Solution S, List<Request> listOfRequests, List<List<Long>> c) {
        double avaliacao = 0;
        double V = 12;
        //double alfa = S.getNonAttendedRequestsList().size();
        double alfa = 10 * listOfRequests.size();
        double beta = 1 / 10;
        double gama = 1;//12 é o número de pontos de parada incluindo o depósito
        int Y = 1000;
        int W = 800;
        //avaliacao = S.getTotalDistance() + alfa * S.getNumberOfNonAttendedRequests() + beta * S.getDeliveryTimeWindowAntecipation() + gama * S.getDeliveryTimeWindowDelay();
        avaliacao = S.getTotalDistance() + S.getSetOfRoutes().size() * W + S.getNonAttendedRequestsList().size() * Y;

        return avaliacao;
    }

    //Função Objetivo
    public static int FO(Solution S, List<List<Integer>> c) {
        int totalCost = 0;

        int W = 1000,
                costU = 800;//200;

        for (Route r : S.getSetOfRoutes()) {
            totalCost += W;
            for (int i = 0, j = r.getNodesVisitationList().size(); i < j - 1; i++) {
                totalCost += c.get(r.getNodesVisitationList().get(i)).get(r.getNodesVisitationList().get(i + 1));
            }
        }

        totalCost += S.getNonAttendedRequestsList().size() * costU;

        return totalCost;
    }

    public static int FOp(Solution S, List<List<Integer>> c) {
        int totalCost = 0;
        int tempoMaximo = 3;
        int PENALIDADE = 2000;
        int W = 1000,
                costU = 10000;//200;//800

        for (Route r : S.getSetOfRoutes()) {
            totalCost += W;
            for (int i = 0, j = r.getNodesVisitationList().size(); i < j - 1; i++) {
                totalCost += c.get(r.getNodesVisitationList().get(i)).get(r.getNodesVisitationList().get(i + 1));
            }
        }

        totalCost += S.getNonAttendedRequestsList().size() * costU;

        int somaTotal = 0;
        for (Route r : S.getSetOfRoutes()) {
            int soma = 0;

            for (Request request : r.getRequestAttendanceList()) {
                //System.out.println("Solicitação = " + request);
                if (request.getDeliveryTime() > request.getDeliveryTimeWindowUpper()) {
                    long dif = request.getDeliveryTime() - request.getDeliveryTimeWindowUpper();
                    if (dif < tempoMaximo) {
                        totalCost += dif * PENALIDADE;
                    } else {
                        totalCost += 15000;
                    }
                    soma += dif;
                }
            }
            r.setTempoExtra(soma);
            somaTotal += soma;
        }
        S.setTempoExtraTotal(somaTotal);
        return totalCost;
    }

    public static long FO1(Solution S, List<List<Long>> c) {
        int totalCost = 0;
        //o custo do veiculo é de 1000
        int W = 1000,//1000,
                costU = 0;//800;//200;

        for (Route r : S.getSetOfRoutes()) {
            totalCost += W;
            for (int i = 0, j = r.getNodesVisitationList().size(); i < j - 1; i++) {
                totalCost += c.get(r.getNodesVisitationList().get(i)).get(r.getNodesVisitationList().get(i + 1));
            }
        }

        totalCost += S.getNonAttendedRequestsList().size() * costU;

        return totalCost;
    }

    public static long FO2(Solution S) {
        int somaTotal = 0;

        for (Route r : S.getSetOfRoutes()) {
            int soma = 0;

            for (Request request : r.getRequestAttendanceList()) {
                if (request.getDeliveryTime() > request.getDeliveryTimeWindowUpper()) {
                    long dif = request.getDeliveryTime() - request.getDeliveryTimeWindowUpper();
                    soma += dif;
                }
            }
            r.setTempoExtra(soma);
            somaTotal += soma;
        }
        return somaTotal;
    }

    public static long FO3(Solution solution) {
        Set<Route> routes = new HashSet<>();
        routes.addAll(solution.getSetOfRoutes());
        List<Long> routeEndTime = new ArrayList<>();
        int soma = 0;
        for (Route route : routes) {
            long time = route.getTimeListTheVehicleLeavesTheNode().get(route.getNodesVisitationList().size() - 2);
            routeEndTime.add(time);
        }
        routeEndTime.sort(Comparator.naturalOrder());

        Long greaterRoute = routeEndTime.get(routeEndTime.size() - 1);
        Long smallerRoute = routeEndTime.get(0);

        return greaterRoute - smallerRoute;
    }

    public static int FO4(Solution solution) {
        return solution.getNonAttendedRequestsList().size();
    }

    public static int FO5(Solution solution) {
        return solution.getSetOfRoutes().size();
    }

    public static int FO6(Solution solution) {
        Set<Route> rotas = new HashSet<>();
        rotas.addAll(solution.getSetOfRoutes());
        int soma = 0;
        for (Route r : rotas) {
            for (Request request : r.getRequestAttendanceList()) {
                soma += request.getDeliveryTime() - request.getPickupTime();
            }
        }
        return soma;
    }

    public static int FO7(Solution solution) {
        Set<Route> rotas = new HashSet<>();
        rotas.addAll(solution.getSetOfRoutes());
        int soma = 0;
        for (Route r : rotas) {
            for (Request request : r.getRequestAttendanceList()) {
                soma += request.getPickupTime() - request.getPickupTimeWIndowLower();
            }
        }
        return soma;
    }

    public static int FO8(Solution solution) {
        Set<Route> rotas = new HashSet<>();
        rotas.addAll(solution.getSetOfRoutes());
        int soma = 0;
        for (Route r : rotas) {
            for (Request request : r.getRequestAttendanceList()) {
                soma += Math.abs(Math.max(request.getDeliveryTimeWindowLower() - request.getDeliveryTime(), 0));
            }
        }
        return soma;
    }

    public static double FO9(Solution solution, int vehicleCapacity) {

        solution.getSetOfRoutes().forEach(route -> route.calculateOccupationRate(vehicleCapacity));
        List<Double> routesOccupationRate = solution.getSetOfRoutes().stream().map(Route::getOccupationRate)
                .collect(Collectors.toCollection(ArrayList::new));

        double totalOccupationRate = routesOccupationRate.stream().mapToDouble(Double::valueOf).average().getAsDouble();

        return totalOccupationRate;
    }

    public static void evaluateAggregatedObjectiveFunctions(Solution S, double alfa, double beta, double gama, double delta, double epslon) {
        //Agregações feitas (combinações lineares) com base na Análise de Componentes Principais
//        S.setF1(0.0066222948*S.getfObjetivo1() + 0.6240729533*S.getfObjetivo2() + 0.0005134346*S.getfObjetivo3() + 0.0001470923*S.getfObjetivo4() 
//                - 0.0005010187*S.getfObjetivo5() + 0.7813356030*S.getfObjetivo6() + 0.0017981648*S.getfObjetivo7());
//        
//        S.setF2(- 0.0096951156*S.getfObjetivo1() - 0.7812557639*S.getfObjetivo2() + 0.0002265355*S.getfObjetivo3() - 0.0035572217*S.getfObjetivo4() 
//                + 0.0007717224*S.getfObjetivo5() + 0.6241041415*S.getfObjetivo6() - 0.0051142916*S.getfObjetivo7());

        //Agregação feita com base na análise de cluster, utilizando a matriz de correlação amostral Rij
//        S.setAggregatedObjective1(800 * S.getNumberOfVehicles() + 500 * S.getNumberOfNonAttendedRequests() + S.getTotalTravelTime() + 20 * S.getTotalWaintingTime());
//        S.setAggregatedObjective2(S.getTotalDistance() + S.getTotalDeliveryDelay() + 20 * S.getTotalRouteTimeChargeBanlance());
//        S.setF1(alfa * S.getfObjetivo2() + /*beta**/ 10 * S.getfObjetivo3() + /*delta**/ 50 * S.getfObjetivo4());
//        S.setF2(gama * S.getfObjetivo1() +/*epslon*/ 800 * S.getfObjetivo5());
        S.setAggregatedObjective1(S.getTotalDistance() + S.getTotalDeliveryDelay() + 20 * S.getTotalRouteTimeChargeBanlance()
                + 800 * S.getNumberOfVehicles() + 500 * S.getNumberOfNonAttendedRequests() + S.getTotalTravelTime());
        S.setAggregatedObjective2(S.getTotalWaintingTime());
    }

    public static Solution greedyConstructive(Double alphaD, Double alphaP, Double alphaV, Double alphaT,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWichBoardsInNode,
            Map<Integer, List<Request>> requestsWichLeavesInNode, Integer numberOfNodes, Integer vehicleCapacity,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndex, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) {

        requestList.clear();
        listOfNonAttendedRequests.clear();
        requestList.addAll(listOfRequests);

        //Step 1
        Solution solution = new Solution();
        String log = "";

        int currentVehicle;
        Map<Integer, Double> costRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> numberOfPassengersRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> deliveryTimeWindowRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> timeWindowRankList = new HashMap<>(numberOfNodes);
        Map<Integer, Double> nodeRankingFunction = new HashMap<>(numberOfNodes);

        Iterator<Integer> vehicleIterator = setOfVehicles.iterator();
        listOfNonAttendedRequests.clear();
        while (!requestList.isEmpty() && vehicleIterator.hasNext()) {

            separateOriginFromDestination(listOfNonAttendedRequests, requestsWichBoardsInNode, requestsWichLeavesInNode,
                    numberOfNodes, requestList);

            //Step 2
            Route route = new Route();
            currentVehicle = vehicleIterator.next();
            log += "\tGROute " + (currentVehicle + 1) + " ";

            //Step 3
            route.addVisitedNodes(0);

            currentTime = (long) 0;
            //-------------------------------------------------------------------
            double max, min;
            //Integer  lastNode = R.getLastNode();
            lastNode = route.getLastNode();

            boolean feasibleNodeIsFound;

            while (!requestList.isEmpty()) {
                feasibleNodeIsFound = false;
                loadIndex.clear();
                for (int i = 0; i < numberOfNodes; i++) {
                    loadIndex.add(requestsWichBoardsInNode.get(i).size() - requestsWichLeavesInNode.get(i).size());
                }

                //Step 4
                Set<Integer> feasibleNode = new HashSet<>();
                List<Long> earliestTime = new ArrayList<>();

                findFeasibleNodes(numberOfNodes, lastNode, feasibleNodeIsFound, vehicleCapacity, route,
                        requestsWichBoardsInNode, requestsWichLeavesInNode, feasibleNode, timeBetweenNodes,
                        currentTime, timeWindows);

                //System.out.println("FEASIBLE NODES = "+ FeasibleNode);			
                if (feasibleNode.size() > 1) {
                    //Step 4.1
                    CalculaCRL(feasibleNode, costRankList, distanceBetweenNodes, lastNode);
                    //Step 4.2
                    CalculaNRL(feasibleNode, numberOfPassengersRankList, loadIndex, lastNode);
                    //Step 4.3
                    CalculaDRL(feasibleNode, deliveryTimeWindowRankList, requestsWichLeavesInNode, lastNode,
                            timeBetweenNodes, earliestTime);
                    //Step 4.4
                    CalculaTRL(feasibleNode, timeWindowRankList, requestsWichBoardsInNode, lastNode, timeBetweenNodes,
                            earliestTime);
                } else {
                    //Step 4.1
                    CalculaListaSemNosViaveis(costRankList, feasibleNode);
                    //Step 4.2
                    CalculaListaSemNosViaveis(numberOfPassengersRankList, feasibleNode);
                    //Step 4.3
                    CalculaListaSemNosViaveis(deliveryTimeWindowRankList, feasibleNode);
                    //Step 4.4
                    CalculaListaSemNosViaveis(timeWindowRankList, feasibleNode);
                }

                //Step 5
                CalculaNRF(nodeRankingFunction, costRankList, numberOfPassengersRankList, deliveryTimeWindowRankList,
                        timeWindowRankList, alphaD, alphaP, alphaV, alphaT, feasibleNode);

                //Step 6              
                //System.out.println("Tamanho da NRF = " + NRF.size());              
                max = Collections.max(nodeRankingFunction.values());

                currentTime = AdicionaNo(nodeRankingFunction, costRankList, numberOfPassengersRankList,
                        deliveryTimeWindowRankList, timeWindowRankList, max, lastNode, requestsWichBoardsInNode,
                        timeBetweenNodes, earliestTime, currentTime, route);

                lastNode = route.getLastNode();

                //Step 7
                //RETIRAR A LINHA DE BAIXO DEPOIS - inicialização de listRequestAux
                List<Request> listRequestAux = new LinkedList<>();
                //Desembarca as solicitações no nó 
                Desembarca(requestsWichBoardsInNode, requestsWichLeavesInNode, lastNode, currentTime, requestList,
                        listRequestAux, route, log);
                //Embarca as solicitações sem tempo de espera
                Embarca(requestsWichBoardsInNode, lastNode, currentTime, requestList, listRequestAux, route, log, vehicleCapacity);
                //Embarca agora as solicitações onde o veículo precisar esperar e guarda atualiza o tempo (currentTime)                               
                currentTime = EmbarcaRelaxacao(requestsWichBoardsInNode, lastNode, currentTime, requestList,
                        listRequestAux, route, log, vehicleCapacity, timeWindows);

                //---------- Trata as solicitações inviáveis -----------
                RetiraSolicitacoesInviaveis(requestsWichBoardsInNode, requestsWichLeavesInNode, listRequestAux,
                        currentTime, requestList, listOfNonAttendedRequests);
                feasibleNodeIsFound = ProcuraSolicitacaoParaAtender(route, vehicleCapacity, requestsWichBoardsInNode,
                        requestsWichLeavesInNode, currentTime, numberOfNodes, timeBetweenNodes, lastNode, timeWindows,
                        feasibleNodeIsFound);
                RetiraSolicitacaoNaoSeraAtendida(feasibleNodeIsFound, requestsWichBoardsInNode, requestsWichLeavesInNode,
                        listRequestAux, currentTime, requestList, listOfNonAttendedRequests);

                //Step 8
                currentTime = FinalizaRota(requestList, route, currentTime, lastNode, timeBetweenNodes, solution);
            }

            //Step 9
            AnaliseSolicitacoesViaveisEmU(listOfNonAttendedRequests, requestList, vehicleIterator, timeBetweenNodes);
        }

        solution.setNonAttendedRequestsList(listOfNonAttendedRequests);
        evaluateSolution(solution, distanceBetweenNodes, vehicleCapacity, listOfRequests);
        solution.setLogger(log);
        solution.linkTheRoutes();

        return solution;
    }

    public static Solution individualConstructive() {
        Solution solution = new Solution();
        List<Request> requestList = new ArrayList<>();
        requestList.addAll(data.getListOfRequests());
        int currentVehicle;
        String log = "";
        int requestCounter = 0;
        Iterator<Integer> vehicleIterator = data.getSetOfVehicles().iterator();
        int counter = 0;
        while (!requestList.isEmpty()) {
            counter++;
            Route route = new Route();
            currentVehicle = vehicleIterator.next();
            log += "\tGRoute " + (currentVehicle + 1) + " ";

            route.addVisitedNodes(0);

            data.setLastNode(route.getLastNode());

            data.setCurrentTime(Math.max(requestList.get(requestCounter).getPickupTimeWIndowLower()
                    - data.getTimeBetweenNodes().get(0).get(requestList.get(requestCounter).getOrigin()), (long) 0));
            route.setDepartureTimeFromDepot(data.getCurrentTime());

            int origin = requestList.get(requestCounter).getOrigin();
            route.addVisitedNodes(origin);
            data.setCurrentTime(data.getCurrentTime() + data.getTimeBetweenNodes().get(data.getLastNode()).get(origin));
            route.boardPassenger(requestList.get(requestCounter), data.getCurrentTime());
            data.setLastNode(route.getLastNode());

            int destination = requestList.get(requestCounter).getDestination();
            route.addVisitedNodes(destination);
            data.setCurrentTime(data.getCurrentTime() + data.getTimeBetweenNodes().get(data.getLastNode()).get(destination));
            route.leavePassenger(requestList.get(requestCounter), data.getCurrentTime());
            data.setLastNode(route.getLastNode());

            data.setCurrentTime(data.getCurrentTime() + data.getTimeBetweenNodes().get(data.getLastNode()).get(0));
            solution.getSetOfRoutes().add(route);

            route.addVisitedNodes(0);
            requestList.remove(0);

            evaluateSolution(solution, data.getDistanceBetweenNodes(), data.getVehicleCapacity(), data.getListOfRequests());
            solution.setLogger(log);
            solution.linkTheRoutes();

            //requestCounter++;
        }

        return solution;
    }

//    
//    public Solution greedyConstructiveRefactoring(Double alphaD, Double alphaP, Double alphaV, Double alphaT) {
//
//        data.getRequestList().clear();
//        data.getListOfNonAttendedRequests().clear();
//        data.getRequestList().addAll(data.getListOfRequests());
//
//        //Step 1
//        Solution solution = new Solution();
//        String log = "";
//
//        int currentVehicle;
//        Map<Integer, Double> costRankList = new HashMap<>(data.getNumberOfNodes());
//        Map<Integer, Double> numberOfPassengersRankList = new HashMap<>(data.getNumberOfNodes());
//        Map<Integer, Double> deliveryTimeWindowRankList = new HashMap<>(data.getNumberOfNodes());
//        Map<Integer, Double> timeWindowRankList = new HashMap<>(data.getNumberOfNodes());
//        Map<Integer, Double> nodeRankingFunction = new HashMap<>(data.getNumberOfNodes());
//
//        Iterator<Integer> vehicleIterator = data.getSetOfVehicles().iterator();
//        data.getListOfNonAttendedRequests().clear();
//        while (!data.getRequestList().isEmpty() && vehicleIterator.hasNext()) {
//
//            separateOriginFromDestination(data.getListOfNonAttendedRequests(), data.getRequestsWichBoardsInNode(),
//                    data.getRequestsWichLeavesInNode(), data.getNumberOfNodes(), data.getRequestList());
//
//            //Step 2
//            Route route = new Route();
//            currentVehicle = vehicleIterator.next();
//            log += "\tGROute " + (currentVehicle + 1) + " ";
//
//            //Step 3
//            route.addVisitedNodes(0);
//
//            data.setCurrentTime((long) 0);
//            //-------------------------------------------------------------------
//            double max, min;
//            //Integer  lastNode = R.getLastNode();
//            data.setLastNode(route.getLastNode());
//
//            boolean feasibleNodeIsFound;
//
//            while (!data.getRequestList().isEmpty()) {
//                feasibleNodeIsFound = false;
//                data.getLoadIndexList().clear();
//                for (int i = 0; i < data.getNumberOfNodes(); i++) {
//                    data.getLoadIndexList().add(data.getRequestsWichBoardsInNode().get(i).size() 
//                            - data.getRequestsWichLeavesInNode().get(i).size());
//                }
//
//                //Step 4
//                Set<Integer> feasibleNode = new HashSet<>();
//                List<Long> earliestTime = new ArrayList<>();
//
//                findFeasibleNodes(data.getNumberOfNodes(), data.getLastNode(), feasibleNodeIsFound,vehicleCapacity, route,
//                        requestsWichBoardsInNode, requestsWichLeavesInNode, feasibleNode, timeBetweenNodes,
//                        currentTime, timeWindows);
//
//                //System.out.println("FEASIBLE NODES = "+ FeasibleNode);			
//                if (feasibleNode.size() > 1) {
//                    //Step 4.1
//                    CalculaCRL(feasibleNode, costRankList, distanceBetweenNodes, lastNode);
//                    //Step 4.2
//                    CalculaNRL(feasibleNode, numberOfPassengersRankList, loadIndex, lastNode);
//                    //Step 4.3
//                    CalculaDRL(feasibleNode, deliveryTimeWindowRankList, requestsWichLeavesInNode, lastNode,
//                            timeBetweenNodes, earliestTime);
//                    //Step 4.4
//                    CalculaTRL(feasibleNode, timeWindowRankList, requestsWichBoardsInNode, lastNode, timeBetweenNodes,
//                            earliestTime);
//                } else {
//                    //Step 4.1
//                    CalculaListaSemNosViaveis(costRankList, feasibleNode);
//                    //Step 4.2
//                    CalculaListaSemNosViaveis(numberOfPassengersRankList, feasibleNode);
//                    //Step 4.3
//                    CalculaListaSemNosViaveis(deliveryTimeWindowRankList, feasibleNode);
//                    //Step 4.4
//                    CalculaListaSemNosViaveis(timeWindowRankList, feasibleNode);
//                }
//
//                //Step 5
//                CalculaNRF(nodeRankingFunction, costRankList, numberOfPassengersRankList, deliveryTimeWindowRankList,
//                        timeWindowRankList, alphaD, alphaP, alphaV, alphaT, feasibleNode);
//
//                //Step 6              
//                //System.out.println("Tamanho da NRF = " + NRF.size());              
//                max = Collections.max(nodeRankingFunction.values());
//
//                currentTime = AdicionaNo(nodeRankingFunction, costRankList, numberOfPassengersRankList,
//                        deliveryTimeWindowRankList, timeWindowRankList, max, lastNode, requestsWichBoardsInNode,
//                        timeBetweenNodes, earliestTime, currentTime, route);
//
//                lastNode = route.getLastNode();
//
//                //Step 7
//                //RETIRAR A LINHA DE BAIXO DEPOIS - inicialização de listRequestAux
//                List<Request> listRequestAux = new LinkedList<>();
//                //Desembarca as solicitações no nó 
//                Desembarca(requestsWichBoardsInNode, requestsWichLeavesInNode, lastNode, currentTime, requestList,
//                        listRequestAux, route, log);
//                //Embarca as solicitações sem tempo de espera
//                Embarca(requestsWichBoardsInNode, lastNode, currentTime, requestList, listRequestAux, route, log, vehicleCapacity);
//                //Embarca agora as solicitações onde o veículo precisar esperar e guarda atualiza o tempo (currentTime)                               
//                currentTime = EmbarcaRelaxacao(requestsWichBoardsInNode, lastNode, currentTime, requestList,
//                        listRequestAux, route, log, vehicleCapacity, timeWindows);
//
//                //---------- Trata as solicitações inviáveis -----------
//                RetiraSolicitacoesInviaveis(requestsWichBoardsInNode, requestsWichLeavesInNode, listRequestAux,
//                        currentTime, requestList, listOfNonAttendedRequests);
//                feasibleNodeIsFound = ProcuraSolicitacaoParaAtender(route, vehicleCapacity, requestsWichBoardsInNode,
//                        requestsWichLeavesInNode, currentTime, numberOfNodes, timeBetweenNodes, lastNode, timeWindows,
//                        feasibleNodeIsFound);
//                RetiraSolicitacaoNaoSeraAtendida(feasibleNodeIsFound, requestsWichBoardsInNode, requestsWichLeavesInNode,
//                        listRequestAux, currentTime, requestList, listOfNonAttendedRequests);
//
//                //Step 8
//                currentTime = FinalizaRota(requestList, route, currentTime, lastNode, timeBetweenNodes, solution);
//            }
//
//            //Step 9
//            AnaliseSolicitacoesViaveisEmU(listOfNonAttendedRequests, requestList, vehicleIterator, timeBetweenNodes);
//        }
//
//        solution.setNonAttendedRequestsList(listOfNonAttendedRequests);
//        evaluateSolution(solution, distanceBetweenNodes, vehicleCapacity, listOfRequests);
//        solution.setLogger(log);
//        solution.linkTheRoutes();
//
//        return solution;
//    }
//
    private static void evaluateSolution(Solution solution, List<List<Long>> distanceBetweenNodes, Integer vehicleCapacity, List<Request> listOfRequests) {
        solution.setTotalDistance(FO1(solution, distanceBetweenNodes));
        solution.setTotalDeliveryDelay(FO2(solution));
        solution.setTotalRouteTimeChargeBanlance(FO3(solution));
        solution.setNumberOfNonAttendedRequests(FO4(solution));
        solution.setNumberOfVehicles(FO5(solution));
        solution.setTotalTravelTime(FO6(solution));
        solution.setTotalWaintingTime(FO7(solution));
        solution.setDeliveryTimeWindowAntecipation(FO8(solution));
        solution.setTotalOccupationRate(FO9(solution, vehicleCapacity));
        evaluateAggregatedObjectiveFunctions(solution, 1, 1, 1, 1, 1);
        solution.setObjectiveFunction(FuncaoDeAvaliacao(solution, listOfRequests, distanceBetweenNodes));
    }

    public static Solution rebuildSolution(List<Integer> vizinho, List<Request> listRequests, List<Request> P,
            Set<Integer> K, List<Request> U, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            List<List<Long>> d, List<List<Long>> c, Integer n, Integer Qmax, Long TimeWindows) {
        P.clear();
        P.addAll(listRequests);

        //Step 1
        Solution solution = new Solution();
        solution.setLinkedRouteList(vizinho);
        String log = "";

        int currentK;

        Iterator<Integer> itK = K.iterator();
        U.clear();
        //Pin.clear();
        //Pout.clear();

        List<Request> auxP = new LinkedList<>(P);
        for (Request request : auxP) {//para cada solicitação, olha se apos o movimento nn contem os nos de embarque, desembarque e a janela de tempo
            if (!vizinho.contains(request.getOrigin()) || !vizinho.contains(request.getDestination()) || !(d.get(0).get(request.getOrigin()) <= request.getPickupTimeWindowUpper())) {
                U.add((Request) request.clone());
                P.remove((Request) request.clone());
            }
        }

        while (!P.isEmpty() && itK.hasNext() && !vizinho.isEmpty()) {
            /*do{*/
            //U.clear();
            //SeparaOrigemDestino(U,Pin,Pout,n,P);
            //----------------------------------------------------------------------------------------------------------
            //     Tomar cuidado com essa parte aqui, modularizando ela o funcionamento alterou significativamente   
            //----------------------------------------------------------------------------------------------------------
            Pin.clear();
            Pout.clear();
            List<Request> origem = new LinkedList<Request>();
            List<Request> destino = new LinkedList<Request>();
            for (int j = 0; j < n; j++) {

                for (Request request : P) {
                    if (request.getOrigin() == j) {
                        origem.add((Request) request.clone());
                    }
                    if (request.getDestination() == j) {
                        destino.add((Request) request.clone());
                    }
                }

                Pin.put(j, new LinkedList<Request>(origem));
                Pout.put(j, new LinkedList<Request>(destino));

                origem.clear();
                destino.clear();
            }
            //----------------------------------------------------------------------------------------------------------
            //Step 2
            Route R = new Route();
            currentK = itK.next();
            log += "\tROTA " + (currentK + 1) + " ";

            /*if(currentK+1 == 3)
             System.out.println("ROTA BREMA");*/
            //Step 3
            R.addVisitedNodes(0);
            long currentTime = 0;

            Integer lastNode = R.getLastNode();
            Integer newNode;
            boolean encontrado;

            while (!P.isEmpty()) {

//				lastNode = R.getLastNode();
                newNode = -1;
                encontrado = false;

                //List<Integer> vizinhoCopia = new ArrayList<Integer>(vizinho);
                for (int k = 0; !encontrado && k < vizinho.size(); k++) {
                    int i = vizinho.get(k);

                    if (i != lastNode) {
                        if (R.getActualOccupation() < Qmax) {
                            for (Request request : Pin.get(i)) {
                                if (lastNode == 0 && d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {
                                    newNode = vizinho.remove(k);
                                    encontrado = true;
                                    break;
                                }
                                //if( (currentTime + d.get(lastNode).get(i)) <= request.getPickupTimeWindowUpper()){
                                if (currentTime + d.get(lastNode).get(i) >= request.getPickupTimeWIndowLower() - TimeWindows
                                        && currentTime + d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {
                                    newNode = vizinho.remove(k);
                                    encontrado = true;
                                    break;
                                }
                            }
                        }

                        /**
                         * E OS N�S DE ENTREGA? DEVEM SER VI�VEIS TAMB�M?*
                         */
                        if (!encontrado && R.getActualOccupation() > 0) {
                            for (Request request : Pout.get(i)) {
                                if (!Pin.get(request.getOrigin()).contains(request)) {
                                    newNode = vizinho.remove(k);
                                    encontrado = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (newNode == -1) {
                    System.out.println("newNode Invalido");
                }
                //-------------------------------------------------------------------------------------------------
                //Step 6
                List<Long> EarliestTime = new ArrayList<>();

                if (lastNode == 0) {
                    //System.out.println("VIZINHO PROBLEMATICO "+vizinho);
                    for (Request request : Pin.get(newNode)) {
                        if (d.get(lastNode).get(newNode) <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {
                            EarliestTime.add(request.getPickupTimeWIndowLower());
                        }
                    }

                    currentTime = Math.max(Collections.min(EarliestTime) - d.get(lastNode).get(newNode), 0);
                    R.setDepartureTimeFromDepot(currentTime);

                    EarliestTime.clear();
                }

                currentTime += d.get(lastNode).get(newNode);

                R.addVisitedNodes(newNode);
                lastNode = R.getLastNode();

                List<Request> listRequestAux = new LinkedList<>(Pout.get(lastNode));

                for (Request request : listRequestAux) {

                    if (!Pin.get(request.getOrigin()).contains(request)) {
                        Pout.get(lastNode).remove((Request) request.clone());
                        P.remove((Request) request.clone());

                        //if(currentK == 1){
                        log += "ENTREGA: " + currentTime + ": " + (Request) request.clone() + " ";
                        //}

                        R.leavePassenger((Request) request.clone(), currentTime);

                        //EXTRA
                        log += "Q=" + R.getActualOccupation() + " ";
                    }
                }
                listRequestAux.clear();

                listRequestAux.addAll(Pin.get(lastNode));

                for (Request request : listRequestAux) {
                    if (R.getActualOccupation() < Qmax && currentTime >= request.getPickupTimeWIndowLower() && currentTime <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {
                        Pin.get(lastNode).remove((Request) request.clone());
                        log += "COLETA: " + currentTime + ": " + (Request) request.clone() + " ";
                        R.boardPassenger((Request) request.clone(), currentTime);
                        log += "Q=" + R.getActualOccupation() + " ";
                    }
                }

                listRequestAux.clear();

                listRequestAux.addAll(Pin.get(lastNode));

                long waitTime = TimeWindows;
                long aux;

                for (Request request : listRequestAux) {
                    if (R.getActualOccupation() < Qmax && currentTime + waitTime >= request.getPickupTimeWIndowLower() && currentTime + waitTime <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {

                        aux = currentTime + waitTime - request.getPickupTimeWIndowLower();
                        currentTime = Math.min(currentTime + waitTime, request.getPickupTimeWIndowLower());
                        waitTime = aux;
                        Pin.get(lastNode).remove((Request) request.clone());
                        log += "COLETAw: " + currentTime + ": " + (Request) request.clone() + " ";
                        R.boardPassenger((Request) request.clone(), currentTime);
                        log += "Q=" + R.getActualOccupation() + " ";
                    }
                }

                listRequestAux.clear();

                for (Integer key : Pin.keySet()) {
                    listRequestAux.addAll(Pin.get(key));
                    for (Integer i = 0, k = listRequestAux.size(); i < k; i++) {
                        Request request = listRequestAux.get(i);
                        if (currentTime > request.getPickupTimeWindowUpper() || !vizinho.contains(request.getOrigin()) || !vizinho.contains(request.getDestination())) {
                            U.add((Request) request.clone());
                            P.remove((Request) request.clone());
                            Pin.get(key).remove((Request) request.clone());
                            Pout.get(request.getDestination()).remove((Request) request.clone());
                        }
                    }
                    listRequestAux.clear();

                }

                encontrado = false;
                for (int k = 0; !encontrado && k < vizinho.size(); k++) {
                    int i = vizinho.get(k);

                    if (i != lastNode) {

                        if (R.getActualOccupation() < Qmax) {
                            for (Request request : Pin.get(i)) {
                                if (currentTime + d.get(lastNode).get(i) >= request.getPickupTimeWIndowLower() - TimeWindows
                                        && currentTime + d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper() && vizinho.contains(request.getDestination())) {
                                    encontrado = true;
                                    break;
                                }
                            }
                        }

                        if (!encontrado && R.getActualOccupation() > 0) {
                            for (Request request : Pout.get(i)) {
                                if (!Pin.get(request.getOrigin()).contains(request)) {
                                    encontrado = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!encontrado) {
                    for (Integer key : Pin.keySet()) {

                        listRequestAux.addAll(Pin.get(key));

                        for (Integer i = 0, k = listRequestAux.size(); i < k; i++) {
                            Request request = listRequestAux.get(i);

                            U.add((Request) request.clone());
                            P.remove((Request) request.clone());
                            Pin.get(key).remove((Request) request.clone());
                            Pout.get(request.getDestination()).remove((Request) request.clone());

                        }
                        listRequestAux.clear();

                    }
                }

                //Step 8
                if (P.isEmpty()) {
                    R.addVisitedNodes(0);
                    currentTime += d.get(lastNode).get(0);
                    solution.getSetOfRoutes().add(R);
                }

            }

            //Step 9
            if (!U.isEmpty() && itK.hasNext()) {
                List<Request> auxU = new LinkedList<>(U);
                for (Request request : auxU) {
                    if (vizinho.contains(request.getOrigin()) && vizinho.contains(request.getDestination()) && d.get(0).get(request.getOrigin()) <= request.getPickupTimeWindowUpper()) {
                        P.add((Request) request.clone());
                        U.remove((Request) request.clone());
                    }
                }
            }

        }
        solution.setNonAttendedRequestsList(U);
        evaluateSolution(solution, c, Qmax, listRequests);
        solution.setLogger(log);

        return solution;
    }

    public static void GeneticAlgorithm(List<Solution> Pop, Integer TamPop, Integer MaxGer, double Pm, double Pc, List<Request> listRequests,
            Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K,
            List<Request> U, List<Request> P, List<Integer> m, List<List<Long>> d, List<List<Long>> c,
            Long TimeWindows, Long currentTime, Integer lastNode) {

        String diretorio, nomeArquivo;
        try {
            //Inicializar população
            //InicializaPopulacao(Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
            //Fitness(Pop);
            //OrdenaPopulacao(Pop);

            //System.out.println("População Inicial");
            //ImprimePopulacao(Pop);
            Solution SBest = new Solution();
            SBest.setObjectiveFunction(1000000000);
            SBest.setTotalDistance(1000000000);
            SBest.setTotalDeliveryDelay(1000000000);

            //System.out.println("Teste do elitismo, SBest = "+ SBest);
            List<Integer> pais = new ArrayList<>();
            double tempoInicio, tempoFim;
            //SBest = copyBestSolution(Pop, SBest);
            //System.out.println("Melhor Individuo = " + SBest);
            int somaTotal;
            double media, desvio;
            diretorio = "\\GA_Funcao_Avaliação";
            nomeArquivo = "Teste_GA";
            boolean success = (new File(diretorio)).mkdirs();
            if (!success) {
                System.out.println("Diretórios ja existem!");
            }
            PrintStream saida;
            saida = new PrintStream(diretorio + "\\GA-DOUTORADO" + nomeArquivo + ".txt");

            for (int cont = 0; cont < 1; cont++) {
                //--------------- Inicializa com a mesma população inicial ------------------
                InicializaPopulacaoPerturbacao(Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);

                Pop.sort(Comparator.comparingDouble(Solution::getObjectiveFunction));
                NewFitness(Pop);
                //OrdenaPopulacao(Pop);

                //CarregaSolucao(Pop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
//                double x, y, z, w;
//                Solution S = new Solution();
//                x = 0.5402697457767974;
//                y = 0.12127711977568245;
//                z = 0.17712922815436938;
//                w = 0.16132390629315074;
//                S.setSolution(greedyConstructive(x, y, z, w, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode));
                //MÉTODO NOVO DAS SOLUÇÕES MELHORES
                //GeraPopGulosa(Pop,TamPop,listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
                //Fitness(Pop);
                //OrdenaPopulacao(Pop);
                printPopulation(Pop);
                SBest.setObjectiveFunction(1000000000);
                SBest.setTotalDeliveryDelay(1000000000);
                //SBest.setSolution(S);
                //System.out.println("População Inicial");
                //ImprimePopulacao(Pop);

                System.out.println("Execução = " + cont);
                int GerAtual = 0;
                while (GerAtual < MaxGer) {
                    //Ordenação da população
                    populationSorting(Pop);

                    //Cálculo do fitness - aptidão
                    NewFitness(Pop);
                    SBest = copyBestSolution(Pop, SBest);
                    saida.print(SBest.getObjectiveFunction() + "\t");

                    //Selecionar
                    rouletteWheelSelectionAlgorithm(pais, Pop, TamPop);
                    //Cruzamento
                    //Cruzamento(Pop, Pc, pais, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows);

                    twoPointsCrossover(Pop, Pop, TamPop, Pc, pais, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows);
                    //Mutação
                    //Mutacao(Pop, Pm, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
                    //Mutacao2Opt(Pop, Pm, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
                    //MutacaoShuffle(Pop, Pm, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);                   
                    mutation2Shuffle(Pop, Pm, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);
                    //MutacaoILS(Pop, Pm, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);

                    //Elitismo
                    insertBestIndividualInPopulation(Pop, SBest);
                    NewFitness(Pop);

                    //BuscaLocal
                    //System.out.println("GerAtual = " + GerAtual);
//                    if ((GerAtual % 150 == 0) && (GerAtual != 0)) {
//                        Solution s = new Solution(SBest);
//                        SBest.setSolution(IteratedLocalSearch(s, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows));
//                    }
                    System.out.println("Geração = " + GerAtual + "\tMelhorFO = " + SBest.getObjectiveFunction());
                    GerAtual++;
                }
                Pop.clear();
                saida.print(SBest + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        populationSorting(Pop);
        Fitness(Pop);
        //System.out.println("Geração final = ");
        printPopulation(Pop);
    }

    public static void InicializaSolucaoArquivo(List<Solution> Pop, String NomeArquivo, List<Request> listRequests, List<Request> P, Set<Integer> K,
            List<Request> U, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            List<List<Long>> d, List<List<Long>> c, Integer n, Integer Qmax, Long TimeWindows) {

        List<Integer> lista = new ArrayList<>();

        Solution S = new Solution();
        //S.setSolution(rebuildSolution(individuo, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
        //Pop.get(i).setSolution(S);

        try {
            FileReader arq = new FileReader("SolucaoInicial.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            String[] linha = lerArq.readLine().split(",");

            //List<String> linha = new ArrayList<>();
            int no, cont;
            String teste;
            cont = 0;
            lista.clear();
            for (int i = 0; i < linha.length; i++) {
                no = Integer.parseInt(linha[i]);
                lista.add(no);
                teste = linha[i];
            }

            S.setSolution(rebuildSolution(lista, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
            System.out.println("Solução = " + S);
            Pop.get(cont).setSolution(S);

            //System.out.println("Pop(0) = " + Pop.get(0).getLinkedRoute());
            while (linha != null) {
                cont++;
                lista.clear();

                //System.out.printf("%s\n", linha);
                linha = lerArq.readLine().split(",");
                for (int i = 0; i < linha.length; i++) {
                    no = Integer.parseInt(linha[i]);
                    teste = linha[i];
                    lista.add(no);
                    //System.out.println("Nó = " + no);
                }
                S.setSolution(rebuildSolution(lista, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
                System.out.println("Solução = " + S);
                Pop.get(cont).setSolution(S);
                //Pop.get(cont).setLinkedRoute(lista);
                // System.out.println("Pop("+cont+") = " + Pop.get(cont).getLinkedRoute());
            }
            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        System.out.println();

    }

    public static Solution VariableNeighborhoodDescend(Solution s_0, List<Request> listRequests, List<Request> P, Set<Integer> K,
            List<Request> U, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, List<List<Long>> d,
            List<List<Long>> c, Integer n, Integer Qmax, Long TimeWindows) {

        Random rnd = new Random();
        Solution melhor = new Solution(s_0);
        Solution s_linha = new Solution();
        Solution s = new Solution();
        int cont1 = 0;
        int k, r;
        r = 6;
        k = 1;
        while ((k <= r)/* && (cont1 <= 50)*/) {
            if (k == 4) {
                k++;
            }
            System.out.println("k = " + k);
            s.setSolution(firstImprovementAlgorithm(s_0, k, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
            //s.setSolution(bestImprovementAlgorithm(s_0, k, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
            if (s.getObjectiveFunction() < melhor.getObjectiveFunction()) {
                melhor.setSolution(s);
                k = 1;
            } else {
                k = k + 1;
            }
            cont1++;
        }
        return melhor;
    }

    public static Solution RVND(Solution s_0, List<Request> listRequests, List<Request> P, Set<Integer> K, List<Request> U,
            Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, List<List<Long>> d, List<List<Long>> c,
            Integer n, Integer Qmax, Long TimeWindows) {

        Solution melhor = new Solution(s_0);
        Solution s = new Solution();
        List<Integer> vizinhanca = new ArrayList<>();
        int k, r;
        r = 6;
        k = 1;

        for (int i = 0; i < r; i++) {
            if (i != 4) {
                vizinhanca.add(i + 1);
            }

        }
        Collections.shuffle(vizinhanca);

        //System.out.println("Vizinhança = " + vizinhanca);
        while ((k <= r)) {

            System.out.println("k = " + k + "\tN = " + vizinhanca.get(k - 1));
            //System.out.println("iteração VariableNeighborhoodDescend = " + cont1);
            //s.setSolution(firstImprovementAlgorithm(s_0,k,listRequests,P,K,U,Pin,Pout, d, c, n, Qmax,TimeWindows));
            s.setSolution(bestImprovementAlgorithm(s_0, vizinhanca.get(k - 1), listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
            if (s.getObjectiveFunction() < melhor.getObjectiveFunction()) {
                melhor.setSolution(s);
                k = 1;
            } else {
                k = k + 1;
            }
        }
        return melhor;
    }

    public static Solution VNS(Solution s_0, List<Request> listRequests, List<Request> P, Set<Integer> K,
            List<Request> U, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, List<List<Long>> d,
            List<List<Long>> c, Integer n, Integer Qmax, Long TimeWindows) {

        Solution melhor = new Solution(s_0);
        Solution s_linha = new Solution();
        Solution s_2linha = new Solution();
        Solution s = new Solution();
        int cont = 0;
        int MAXCONT = 5;

        int k, r;
        r = 6;

        while (cont < MAXCONT) {
            k = 1;
            while ((k <= r)) {

                //s_linha.setSolution(vizinhoAleatorio(s_0, k, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
                s_2linha.setSolution(VariableNeighborhoodDescend(s_linha, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));

                if (s_2linha.getObjectiveFunction() < melhor.getObjectiveFunction()) {
                    melhor.setSolution(s_2linha);
                    k = 1;

                } else {
                    k = k + 1;
                }
                cont++;
            }
        }
        System.out.println("Soluçao retornada do VNS = " + melhor);
        return melhor;
    }

    public static Solution perturbation(Solution s, List<Request> listRequests, Map<Integer, List<Request>> Pin,
            Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P,
            List<Integer> m, List<List<Long>> d, List<List<Long>> c, Long TimeWindows) {
        Random rnd = new Random();
        Random p1 = new Random();
        Random p2 = new Random();
        int posicao1, posicao2;
        int NUMPERT = 4;//número de perturções

        List<Integer> original = new ArrayList<>(s.getLinkedRouteList());
        //for (int i = 0; i < NUMPERT; i++) {
        posicao1 = p1.nextInt(original.size());

        do {
            posicao2 = p2.nextInt(original.size());
        } while (Objects.equals(original.get(posicao1), original.get(posicao2)));

        //Collections.swap(original, posicao1, posicao2);
        //Collections.shuffle(original);
        original.add(posicao1, original.remove(posicao2));
        //}
        Solution S = new Solution();
        S.setSolution(rebuildSolution(original, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
        s.setSolution(S);

        return s;
    }

    public static Solution geraPesos(Integer semente, List<Request> listRequests, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P, List<Integer> m, List<List<Long>> d,
            List<List<Long>> c, Long TimeWindows, Long currentTime, Integer lastNode) {
        //for (int i = 0; i < 1; i++) {
        Solution S = new Solution();
        Random rnd = new Random(semente);
        //Random rnd = new Random();
        double x, y, z, w;
        do {
            x = rnd.nextDouble();
            y = rnd.nextDouble();
            z = rnd.nextDouble();
            w = 1 - x - y - z;
        } while (x + y + z > 1);

        //System.out.println(x + "\t" + y + "\t" + z + "\t" + w);
        S.setSolution(greedyConstructive(x, y, z, w, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode));
        //System.out.println("SolucaoGulosaAleatoria = "+ S);
        //S.setSolution(GeraSolucaoAleatoria(Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode));
        //Pop.add(S);
        return S;
        //}
    }

    public static Solution PerturbacaoSemente(Solution s, List<Request> listRequests, Map<Integer, List<Request>> Pin,
            Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P,
            List<Integer> m, List<List<Long>> d, List<List<Long>> c, Long TimeWindows) {
        Random rnd = new Random(19700621);
        Random p1 = new Random(19622);
        Random p2 = new Random(19700623);
        int posicao1, posicao2;
        int NUMPERT = 2;//número de perturções

        List<Integer> original = new ArrayList<>(s.getLinkedRouteList());
        //for (int i = 0; i < NUMPERT; i++) {
        posicao1 = p1.nextInt(original.size());

        do {
            posicao2 = p2.nextInt(original.size());
        } while (Objects.equals(original.get(posicao1), original.get(posicao2)));

        //Collections.swap(original, posicao1, posicao2);
        //Collections.shuffle(original);
        original.add(posicao1, original.remove(posicao2));
        //}
        Solution S = new Solution();
        S.setSolution(rebuildSolution(original, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
        s.setSolution(S);

        return s;
    }

    public static Solution IteratedLocalSearch(Solution s_0, List<Request> listRequests, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout, Integer n, Integer Qmax, Set<Integer> K,
            List<Request> U, List<Request> P, List<Integer> m, List<List<Long>> d,
            List<List<Long>> c, Long TimeWindows) {

        Solution s = new Solution(s_0);
        Solution s_linha = new Solution();
        Solution s_2linha = new Solution();
        List<Solution> historico = new ArrayList<>();
        int MAXITER = 4;

        s.setSolution(VariableNeighborhoodDescend(s_0, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
        System.out.println("After the first local search s = " + s);
        int cont = 0;
        while (cont < MAXITER) {
            System.out.println("Iteration ILS = " + cont);
            s_linha.setSolution(perturbation(s, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows));
            System.out.println("After pertubation s'= " + s_linha);
            s_2linha.setSolution(VariableNeighborhoodDescend(s_linha, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));

            if (s_2linha.getObjectiveFunction() < s_0.getObjectiveFunction()) {
                s.setSolution(s_2linha);
                s_0.setSolution(s_2linha);
                System.out.println("Actualized \tFO = " + s.getObjectiveFunction());
            }
            cont++;
        }
        System.out.println("Returned solution from ILS = " + s_0);
        return s_0;
    }

    public static void LeituraPesosArquivo(List<Solution> Pop, List<Request> listRequests, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P, List<Integer> m, List<List<Integer>> d,
            List<List<Integer>> c, Integer TimeWindows) {
        List<Integer> lista = new ArrayList<>();

        Solution S = new Solution();
        //S.setSolution(rebuildSolution(individuo, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
        //Pop.get(i).setSolution(S);

        try {
            Scanner scanner = new Scanner(new FileReader("Pesos.txt")).useDelimiter("\\t");
            String linha = new String();

            while (scanner.hasNext()) {
                //double x = Double.parseDouble(scanner.next()) ;
                linha = scanner.nextLine();
                String[] valores = linha.split("\t");
                Double x = Double.parseDouble(valores[0]);
                Double y = Double.parseDouble(valores[1]);
                Double z = Double.parseDouble(valores[2]);

                //System.out.println("X = " + linha);
                //System.out.println("Valores = " + valores[2]);
            }

        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        System.out.println();
    }

    public static void GeraPopGulosa(List<Solution> Pop, Integer TamPop, List<Request> listRequests, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P, List<Integer> m, List<List<Long>> d,
            List<List<Long>> c, Long TimeWindows, Long currentTime, Integer lastNode) {
        Solution s = new Solution();
        //Pop.clear();
        //initializePopulation(Pop, TamPop, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode);

        for (int i = 0; i < TamPop; i++) {
            s.setSolution(geraPesos(i, listRequests, Pin, Pout, n, Qmax, K, U, P, m, d, c, TimeWindows, currentTime, lastNode));
            //Pop.add(s);
            //System.out.println(s);
            Pop.get(i).setSolution(s);
        }
        //System.out.println("Tamanho da Pop = " + Pop.size());
        //Fitness(Pop);
        //OrdenaPopulacao(Pop);

        //ImprimePopulacao(Pop);
    }

    // Algoritmo GRASP_reativo 
    public static Solution GRASP_reativo(Integer MAX_ITERATIONS, Double alphaD, Double alphaP, Double alphaV, Double alphaT, int tipoBusca,
            int tipoEstrategia, int tipoMovimento, List<Request> listRequests, PrintStream saida, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            Integer n, Integer Qmax, Set<Integer> K, List<Request> U, List<Request> P, List<Integer> m,
            List<List<Long>> d, List<List<Long>> c, Long TimeWindows) {

        Solution SStar = new Solution();
        Solution original = new Solution();
        Solution originalFinal = new Solution();

        SStar.setTotalDistance(9999999);
        SStar.setTotalDeliveryDelay(9999999);
        double tempoInicio, tempoFim, tempo = 0;

        Solution solution = new Solution();
        String log;

        Integer num_iterations = 0;

        Double alpha;
        Iterator<Integer> itK;
        int currentK;
        Map<Integer, Double> CRL = new HashMap<Integer, Double>(n), // Cost Rank List
                NRL = new HashMap<Integer, Double>(n), // Number of Passengers Rank List
                DRL = new HashMap<Integer, Double>(n), // Delivery time-window Rank List
                TRL = new HashMap<Integer, Double>(n), // Time-window Rank List
                NRF = new HashMap<Integer, Double>(n);	// Time-window Rank List
        Random r = new Random(System.nanoTime());

        //Double A[] = {0.30,0.35,0.40,0.45,0.50,0.55,0.60,0.65,0.70};
        Map<Double, List<Double>> A = new TreeMap<Double, List<Double>>();
        double doubleAlfa;

        for (int alfa = 30; alfa <= 70; alfa += 5) {

            doubleAlfa = Double.parseDouble(Float.toString(new Float(alfa * 0.01)));
            A.put(doubleAlfa, new ArrayList<Double>(5));

        }

        double teta = 10, sigma;
        double probAcumulada, auxProbAcumulada;

        List<Double> auxA = new ArrayList<Double>(5);
        auxA.add(0.0);
        auxA.add(0.0);
        auxA.add(1.0 / A.size());
        auxA.add(0.0);
        auxA.add(0.0);

        for (Map.Entry<Double, List<Double>> e : A.entrySet()) {
            e.setValue(new ArrayList<Double>(auxA));
        }
        auxA.clear();

        while (num_iterations < MAX_ITERATIONS) {
            //if (num_iterations % 10 == 0) {
            System.out.println("Iteração = " + num_iterations);
            //}
            //solutionCost = 0;
            log = "";

            P.clear();
            P.addAll(listRequests);

            probAcumulada = r.nextDouble();
            auxProbAcumulada = 0;
            //alpha = (double)(r.nextInt(11)/10);
            alpha = null;
            for (Map.Entry<Double, List<Double>> e : A.entrySet()) {
                if (auxProbAcumulada <= probAcumulada && probAcumulada <= e.getValue().get(2) + auxProbAcumulada) {
                    alpha = e.getKey();
                    break;
                }
                auxProbAcumulada += e.getValue().get(2);
            }

            //S.resetSolucao(99999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999, 9999999,  9999999);
            itK = K.iterator();
            //construction phase
            while (!P.isEmpty() && itK.hasNext()) {
                U.clear();
                Pin.clear();
                Pout.clear();
                List<Request> origem = new LinkedList<Request>();
                List<Request> destino = new LinkedList<Request>();
                for (int j = 0; j < n; j++) {

                    for (Request request : P) {
                        if (request.getOrigin() == j) {
                            origem.add((Request) request.clone());
                        }
                        if (request.getDestination() == j) {
                            destino.add((Request) request.clone());
                        }
                    }

                    Pin.put(j, new LinkedList<Request>(origem));
                    Pout.put(j, new LinkedList<Request>(destino));

                    origem.clear();
                    destino.clear();
                }

                //Step 2
                Route R = new Route();
                currentK = itK.next();
                log += "\tGROTA " + (currentK + 1) + " ";

                //Step 3
                R.addVisitedNodes(0);
                long currentTime = 0;

                Double min, max;

                Integer lastNode = R.getLastNode();

                boolean encontrado;

                while (!P.isEmpty()) {

                    //Build Candidate List (CL) from current Node using NRF
                    List<Integer> CL = new LinkedList<Integer>();

                    m.clear();
                    for (int i = 0; i < n; i++) {
                        m.add(Pin.get(i).size() - Pout.get(i).size());
                    }

                    //Step 4
                    Set<Integer> FeasibleNode = new HashSet<Integer>();
                    List<Long> EarliestTime = new ArrayList<Long>();

                    //lastNode = R.getLastNode();					
                    for (int i = 1; i < n; i++) {
                        if (i != lastNode) {
                            encontrado = false;

                            if (R.getActualOccupation() < Qmax) {
                                for (Request request : Pin.get(i)) {
                                    if (lastNode == 0 && d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper()) {
                                        FeasibleNode.add(i);
                                        encontrado = true;
                                        break;
                                    }
                                    //if( (currentTime + d.get(lastNode).get(i)) <= request.getPickupTimeWindowUpper()){
                                    if (!encontrado && currentTime + d.get(lastNode).get(i) >= request.getPickupTimeWIndowLower() - TimeWindows
                                            && currentTime + d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper()) {
                                        FeasibleNode.add(i);
                                        encontrado = true;
                                        break;
                                    }
                                }
                            }

                            /**
                             * E OS N�S DE ENTREGA? DEVEM SER VI�VEIS TAMB�M?*
                             */
                            if (!encontrado && R.getActualOccupation() > 0) {
                                for (Request request : Pout.get(i)) {
                                    if (!Pin.get(request.getOrigin()).contains(request)) {
                                        FeasibleNode.add(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //System.out.println("\nFEASIBLE NODES = "+FeasibleNode+"Qik"+Qik+" ROTA "+R+" SOLUCAO S "+S);
                    //System.out.println("FEASIBLE NODES = "+FeasibleNode+"Qik"+Qik+" ROTA "+R+" SOLUCAO S "+S);
                    if (FeasibleNode.size() > 1) {
                        //Step 4.1
                        for (Integer i : FeasibleNode) {
                            CRL.put(i, (double) c.get(lastNode).get(i));
                        }

                        //Normalizacao
                        /*if(CRL.size() > 0){*/
                        min = Collections.min(CRL.values());
                        max = Collections.max(CRL.values());

                        if (min != max) {
                            for (Integer i : FeasibleNode) {
                                CRL.put(i, ((max - CRL.get(i)) / (max - min)));
                            }
                        } else {
                            for (Integer i : FeasibleNode) {
                                CRL.put(i, 1.0);
                            }
                        }
                        /*}

                         for(Integer i : FeasibleNode)
                         if(!CRL.containsKey(i))
                         CRL.put(i, 0.0);*/

                        //Step 4.2
                        for (Integer i : FeasibleNode) {
                            NRL.put(i, (double) m.get(i));
                        }

                        //Normalizacao
                        /*if(NRL.size() > 0){*/
                        min = Collections.min(NRL.values());
                        max = Collections.max(NRL.values());

                        if (min != max) {
                            for (Integer i : FeasibleNode) {
                                NRL.put(i, ((NRL.get(i) - min) / (max - min)));
                            }
                        } else {
                            for (Integer i : FeasibleNode) {
                                NRL.put(i, 1.0);
                            }
                        }
                        /*}

                         for(Integer i : FeasibleNode)
                         if(!NRL.containsKey(i))
                         NRL.put(i, 0.0);*/

                        //Step 4.3
                        for (Integer i : FeasibleNode) {
                            if (Pout.get(i).size() > 0) {
                                for (Request request : Pout.get(i)) {
                                    EarliestTime.add(request.getDeliveryTimeWindowLower());
                                }

                                DRL.put(i, (double) (Collections.min(EarliestTime) + d.get(lastNode).get(i)));

                                EarliestTime.clear();
                            }
                        }

                        //Normalizacao
                        if (DRL.size() > 0) {
                            min = Collections.min(DRL.values());
                            max = Collections.max(DRL.values());

                            if (min != max) {
                                for (Integer i : DRL.keySet()) {
                                    DRL.put(i, ((max - DRL.get(i)) / (max - min)));
                                }
                            } else {
                                for (Integer i : DRL.keySet()) {
                                    DRL.put(i, 1.0);
                                }
                            }
                        }

                        for (Integer i : FeasibleNode) {
                            if (!DRL.containsKey(i)) {
                                DRL.put(i, 0.0);
                            }
                        }

                        //Step 4.4
                        for (Integer i : FeasibleNode) {
                            if (Pin.get(i).size() > 0) {
                                for (Request request : Pin.get(i)) {
                                    EarliestTime.add(request.getPickupTimeWIndowLower());
                                }

                                TRL.put(i, (double) (Collections.min(EarliestTime) + d.get(lastNode).get(i)));

                                EarliestTime.clear();
                            }
                        }

                        //Normalizacao
                        if (TRL.size() > 0) {
                            min = Collections.min(TRL.values());
                            max = Collections.max(TRL.values());

                            if (min != max) {
                                for (Integer i : TRL.keySet()) {
                                    TRL.put(i, ((max - TRL.get(i)) / (max - min)));
                                }
                            } else {
                                for (Integer i : TRL.keySet()) {
                                    TRL.put(i, 1.0);
                                }
                            }
                        }

                        for (Integer i : FeasibleNode) {
                            if (!TRL.containsKey(i)) {
                                TRL.put(i, 0.0);
                            }
                        }
                    } else {
                        //Step 4.1
                        for (Integer i : FeasibleNode) {
                            CRL.put(i, 1.0);
                        }

                        //Step 4.2
                        for (Integer i : FeasibleNode) {
                            NRL.put(i, 1.0);
                        }

                        //Step 4.3
                        for (Integer i : FeasibleNode) {
                            DRL.put(i, 1.0);
                        }

                        //Step 4.4
                        for (Integer i : FeasibleNode) {
                            TRL.put(i, 1.0);
                        }
                    }

                    //System.out.println("Keys\nCRL= "+CRL.keySet()+"\nNRL= "+NRL.keySet()+"\nDRL = "+DRL.keySet()+"\nTRL ="+TRL.keySet());
                    //System.out.println("V= "+V);
                    //Step 5
                    for (Integer i : FeasibleNode) {
                        NRF.put(i, (alphaD * CRL.get(i) + alphaP * NRL.get(i))
                                + (alphaV * DRL.get(i) + alphaT * TRL.get(i)));
                    }
                    //System.out.println("NRF = "+NRF);

                    //Ordenar CL a partir de NRF					
                    for (Integer NRFkey : NRF.keySet()) {
                        if (!CL.isEmpty()) {

                            for (Integer CLkey : CL) {
                                if (NRF.get(NRFkey) > NRF.get(CLkey)) {
                                    CL.add(CL.indexOf(CLkey), NRFkey);
                                    break;
                                }
                            }

                            if (!CL.contains(NRFkey)) {
                                CL.add(NRFkey);
                            }
                        } else {
                            CL.add(NRFkey);
                        }

                    }
                    /*Float teste = (float) 1.0;*/
                    //System.out.println("CL"+CL);
                    //System.out.println("alpha*size = "+Math.max((int)Math.ceil(alpha.floatValue()*CL.size()), 1));

                    //Build Restricted Candidate List (RCL) using current alpha
                    List<Integer> RCL = new LinkedList<Integer>(CL.subList(0, Math.max((int) Math.ceil(alpha.floatValue() * CL.size()), 1)));

                    //System.out.println("RCL"+RCL);
                    int position = r.nextInt(RCL.size());

                    //System.out.println("RCL position["+position+"] = "+RCL.get(position));
                    //Step 6
                    /**
                     * NECESS�RIO TESTAR SE O N� ESCOLHIDO � VI�VEL? AFINAL DE
                     * CONTAS, O CONJ TRABALHADO J� � O DE N�S VI�VEIS*
                     */
                    Integer newNode = RCL.get(position);
                    if (lastNode == 0) {
                        for (Request request : Pin.get(newNode)) {
                            if (d.get(lastNode).get(newNode) <= request.getPickupTimeWindowUpper()) {
                                EarliestTime.add(request.getPickupTimeWIndowLower());
                            }
                        }

                        currentTime = Math.max(Collections.min(EarliestTime) - d.get(lastNode).get(newNode), 0);
                        R.setDepartureTimeFromDepot(currentTime);

                        EarliestTime.clear();
                    }

                    currentTime += d.get(lastNode).get(newNode);

                    R.addVisitedNodes(newNode);
                    lastNode = R.getLastNode();

                    CRL.clear();
                    NRL.clear();
                    DRL.clear();
                    TRL.clear();
                    NRF.clear();

                    //Step 7
                    /**
                     * SOLICITA��ES J� ATENDIDAS *
                     */
                    List<Request> listRequestAux = new LinkedList<Request>(Pout.get(lastNode));

                    for (Request request : listRequestAux) {

                        if (!Pin.get(request.getOrigin()).contains(request)) {
                            Pout.get(lastNode).remove((Request) request.clone());
                            P.remove((Request) request.clone());

                            //if(currentK == 0){
                            log += "ENTREGA: " + currentTime + ": " + (Request) request.clone() + " ";
                            //}

                            R.leavePassenger((Request) request.clone(), currentTime);

                            //EXTRA
                            log += "Q=" + R.getActualOccupation() + " ";
                        }
                    }
                    listRequestAux.clear();

                    listRequestAux.addAll(Pin.get(lastNode));

                    for (Request request : listRequestAux) {
                        if (R.getActualOccupation() < Qmax && currentTime >= request.getPickupTimeWIndowLower() && currentTime <= request.getPickupTimeWindowUpper()) {
                            Pin.get(lastNode).remove((Request) request.clone());

                            //if(currentK == 1){
                            /**
                             * ** Anexado a classe Rota
                             * S.addAtendimento((Request)request.clone()); **
                             */
                            log += "COLETA: " + currentTime + ": " + (Request) request.clone() + " ";
                            //}

                            R.boardPassenger((Request) request.clone(), currentTime);

                            //EXTRA
                            log += "Q=" + R.getActualOccupation() + " ";
                        }
                    }

                    listRequestAux.clear();

                    listRequestAux.addAll(Pin.get(lastNode));

                    long waitTime = TimeWindows;
                    long aux;

                    for (Request request : listRequestAux) {
                        if (R.getActualOccupation() < Qmax && currentTime + waitTime >= request.getPickupTimeWIndowLower() && currentTime + waitTime <= request.getPickupTimeWindowUpper()) {

                            aux = currentTime + waitTime - request.getPickupTimeWIndowLower();
                            currentTime = Math.min(currentTime + waitTime, request.getPickupTimeWIndowLower());
                            waitTime = aux;

                            Pin.get(lastNode).remove((Request) request.clone());

                            //if(currentK == 1){
                            /**
                             * ** Anexado a classe Rota
                             * S.addAtendimento((Request)request.clone()); **
                             */
                            log += "COLETAw: " + currentTime + ": " + (Request) request.clone() + " ";
                            //}

                            R.boardPassenger((Request) request.clone(), currentTime);

                            //EXTRA
                            log += "Q=" + R.getActualOccupation() + " ";
                        }
                    }

                    /**
                     * SOLICITA��ES INVI�VEIS *
                     */
                    listRequestAux.clear();

                    for (Integer key : Pin.keySet()) {

                        listRequestAux.addAll(Pin.get(key));

                        for (Integer i = 0, n1 = listRequestAux.size(); i < n1; i++) {
                            Request request = listRequestAux.get(i);

                            if (currentTime > request.getPickupTimeWindowUpper()) {
                                U.add((Request) request.clone());
                                P.remove((Request) request.clone());
                                Pin.get(key).remove((Request) request.clone());
                                Pout.get(request.getDestination()).remove((Request) request.clone());
                            }
                        }
                        listRequestAux.clear();
                    }

                    encontrado = false;
                    for (int i = 1; !encontrado && i < n; i++) {
                        if (i != lastNode) {

                            if (R.getActualOccupation() < Qmax) {
                                for (Request request : Pin.get(i)) {
                                    if (currentTime + d.get(lastNode).get(i) >= request.getPickupTimeWIndowLower() - TimeWindows
                                            && currentTime + d.get(lastNode).get(i) <= request.getPickupTimeWindowUpper()) {
                                        encontrado = true;
                                        break;
                                    }
                                }
                            }

                            /**
                             * E OS N�S DE ENTREGA? DEVEM SER VI�VEIS TAMB�M?*
                             */
                            if (!encontrado && R.getActualOccupation() > 0) {
                                for (Request request : Pout.get(i)) {
                                    if (!Pin.get(request.getOrigin()).contains(request)) {
                                        encontrado = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (!encontrado) {
                        for (Integer key : Pin.keySet()) {

                            listRequestAux.addAll(Pin.get(key));

                            for (Integer i = 0, n1 = listRequestAux.size(); i < n1; i++) {
                                Request request = listRequestAux.get(i);

                                U.add((Request) request.clone());
                                P.remove((Request) request.clone());
                                Pin.get(key).remove((Request) request.clone());
                                Pout.get(request.getDestination()).remove((Request) request.clone());

                            }
                            listRequestAux.clear();

                        }
                    }

                }

                //Step 8
                if (P.isEmpty()) {
                    R.addVisitedNodes(0);
                    //log += R.toString()+"\n";
                    //System.out.println("Route "+R+" - "+currentTime);
                    //solutionCost += currentTime;
                    currentTime += d.get(lastNode).get(0);
                    solution.getSetOfRoutes().add(R);
                    //System.out.println("Solution S = "+S);
                }

                //Step 9
                if (!U.isEmpty() && itK.hasNext()) {
                    encontrado = false;
                    List<Request> auxU = new ArrayList<Request>(U);
                    for (Request request : auxU) {
                        if (d.get(0).get(request.getOrigin()) <= request.getPickupTimeWindowUpper()) {
                            encontrado = true;
                        }
                    }

                    //Step 9
                    if (encontrado) {
                        P.addAll(U);
                    }
                }
                /*
                 //Step 9
                 if(!U.isEmpty())
                 P.addAll(U);*/
            }
            //solutionCost += FO(S,U.size()); 
            //solutionCost = FO(S,U.size());
            solution.setNonAttendedRequestsList(U);
            solution.setTotalDistance(FO1(solution, c));
            solution.setTotalDeliveryDelay(FO2(solution));
            solution.setTotalRouteTimeChargeBanlance(FO3(solution));
            solution.setNumberOfNonAttendedRequests(FO4(solution));
            solution.setNumberOfVehicles(FO5(solution));
            solution.setTotalTravelTime(FO6(solution));
            solution.setTotalWaintingTime(FO7(solution));
            solution.setDeliveryTimeWindowAntecipation(FO8(solution));
            solution.setTotalOccupationRate(FO9(solution, Qmax));
            evaluateAggregatedObjectiveFunctions(solution, 1, 1, 1, 1, 1);
            solution.setObjectiveFunction(FuncaoDeAvaliacao(solution, listRequests, c));
            solution.setLogger(log);
            solution.linkTheRoutes();
            //solutionCost = FO(S);// ???IMPORTANTE?????

            tempoInicio = (System.nanoTime() * 0.000001);
            /**
             * Tipo Vizinho: 1 - melhorVizinho, 2 - primeiroMelhorVizinho Tipo
             * Operacao: 1 - Troca, 2 - Insercao, 3 - Movimento, 4 - Aleatoria
             *
             */
            Solution busca;
            switch (tipoBusca) {
                case 1:
                    busca = new Solution(buscaLocal(solution, tipoEstrategia, tipoMovimento, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
                    break;
                case 2:
                    //busca = new Solution( buscaTabu(S, tipoEstrategia, tipoMovimento, listRequests) );
                    busca = new Solution(buscaTabu(solution, tipoEstrategia, tipoMovimento, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));
                    break;
                default:
                    busca = new Solution();
            }

            tempoFim = (System.nanoTime() * 0.000001);
            tempo += (tempoFim - tempoInicio);

            original.setSolution(solution);
            solution.setSolution(busca);

            if (solution.getObjectiveFunction() < SStar.getObjectiveFunction()) {
                //System.out.println("ACHEI: "+solutionCost+" -> "+S);
                SStar.setSolution(solution);
                originalFinal.setSolution(original);
                /*logStar = log;

                 SStarCost = solutionCost;
                 SStarUsize = U.size();*/

                //pAlpha.put(alpha, pAlpha.get(alpha)+1.0);
            }

            /**
             * aux[0] = count[] aux[1] = score[] aux[2] = prob[] aux[3] = avg[]
             * aux[4] = Qk[] *
             */
            auxA.clear();
            auxA = A.get(alpha);
            auxA.set(0, auxA.get(0) + 1.0);
            auxA.set(1, auxA.get(1) + solution.getTotalDistance());
            A.put(alpha, new ArrayList<Double>(auxA));

            if (num_iterations > 0 && num_iterations % 20 == 0) {
                sigma = 0;

                for (Map.Entry<Double, List<Double>> e : A.entrySet()) {
                    auxA.clear();
                    auxA = e.getValue();
                    if (auxA.get(0) == 0.0 || auxA.get(1) == 0.0) {
                        auxA.set(4, 0.0);
                    } else {
                        auxA.set(3, auxA.get(1) / auxA.get(0));
                        auxA.set(4, Math.pow(SStar.getTotalDistance() / auxA.get(3), teta));

                        sigma += auxA.get(4);
                    }
                    e.setValue(new ArrayList<Double>(auxA));
                }

                for (Map.Entry<Double, List<Double>> e : A.entrySet()) {
                    auxA.clear();
                    auxA = e.getValue();
                    auxA.set(2, auxA.get(4) / sigma);
                    e.setValue(new ArrayList<Double>(auxA));
                    //System.out.println("");
                }
            }

            num_iterations++;
        }
        //System.out.print("Usize = "+SStarUsize+"\n"+SStarCost+" -> ");
        //System.out.println(SStarCost+"\t"+SStarUsize);
        //System.out.println(SStar.getfObjetivo()+"\t"+SStar.getNonAttendedRequestsList().size());
        /**
         * IMPRIME PRIMEIRO O TEMPO DE BUSCA*
         */

        //saida.print((int)tempo+"\t"+originalFinal);
        //saida2.println();
        /*if(ativaLog){
         //System.out.println(SStar.getLogger());
         //	System.out.println("NaoAtendimento "+SStar.getNonAttendedRequestsList().size());
         //System.out.println("Atendimento "+SStar.getListaAtendimento().size());
         }*/
        return SStar;
    }

    /**
     * Busca Local
     *
     */
    public static Solution buscaLocal(Solution inicial, int tipoEstrategia, int tipoMovimento, List<Request> listRequests, List<Request> P,
            Set<Integer> K, List<Request> U, Map<Integer, List<Request>> Pin, Map<Integer, List<Request>> Pout,
            List<List<Long>> d, List<List<Long>> c, Integer n, Integer Qmax, Long TimeWindows) {
        Solution melhor = new Solution();
        Solution s = new Solution(inicial);

        /**
         * Tipo Estrategia: 1 - melhorVizinho, 2 - primeiroMelhorVizinho Tipo
         * Movimento: 1 - Troca, 2 - Substituicao, 3 - Deslocamento, 4 -
         * Aleatoria
         *
         */
        if (tipoEstrategia == 1) { // utiliza o m�todo bestImprovementAlgorithm

            do {

                melhor.setSolution(s);
                //s.setSolution(bestImprovementAlgorithm(melhor,tipoMovimento, listRequests));
                s.setSolution(bestImprovementAlgorithm(melhor, tipoMovimento, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));

            } while (!s.equals(melhor));

        } else {

            do {

                melhor.setSolution(s);
                //s.setSolution(firstImprovementAlgorithm(melhor,tipoMovimento, listRequests));
                s.setSolution(firstImprovementAlgorithm(melhor, tipoMovimento, listRequests, P, K, U, Pin, Pout, d, c, n, Qmax, TimeWindows));

            } while (!s.equals(melhor));

        }

        return melhor;
    }

    public static List<Solution> generateInitialPopulation(int populationSize, final Integer vehicleCapacity,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWichBoardsInNode,
            Map<Integer, List<Request>> requestsWichLeavesInNode, Integer numberOfNodes,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) {

        Solution solution = greedyConstructive(0.2, 0.15, 0.55, 0.1, listOfRequests, requestsWichBoardsInNode,
                requestsWichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList,
                loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

        Solution solution1 = new Solution();
        List<Solution> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            solution1.setSolution(perturbation(solution, listOfRequests, requestsWichBoardsInNode, requestsWichLeavesInNode,
                    numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes,
                    distanceBetweenNodes, timeWindows));
            Solution solution2 = new Solution();
            solution2.setSolution(solution1);
            population.add(solution2);
        }
        return population;
    }

    public static List<Solution> generateInitialPopulation2(int populationSize, final Integer vehicleCapacity,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWichBoardsInNode,
            Map<Integer, List<Request>> requestsWichLeavesInNode, Integer numberOfNodes,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) {

//        Solution solution = greedyConstructive(0.2, 0.15, 0.55, 0.1,listOfRequests,requestsWichBoardsInNode, 
//                requestsWichLeavesInNode, numberOfNodes, vehicleCapacity,setOfVehicles, listOfNonAttendedRequests, requestList,
//                loadIndexList,timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime,lastNode);
        Solution solution1 = new Solution();
        List<Solution> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            Solution solution = new Solution();
            solution.setSolution(GeraSolucaoAleatoria(population, populationSize, listOfRequests, requestsWichBoardsInNode,
                    requestsWichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests,
                    requestList, loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode));
            System.out.println(solution);
            population.add(solution);
        }

//        for (int i = 0; i < populationSize; i++) {
//            solution1.setSolution(perturbation(solution, listOfRequests,requestsWichBoardsInNode,requestsWichLeavesInNode,
//                    numberOfNodes, vehicleCapacity,setOfVehicles, listOfNonAttendedRequests, requestList,loadIndexList, timeBetweenNodes,
//                    distanceBetweenNodes,timeWindows));
//            Solution solution2 = new Solution();
//            solution2.setSolution(solution1);
//            population.add(solution2);
//        }
        return population;
    }

    public static void generateRandomSolutionsUsingPerturbation(int numberOfRandomSolutions, final Integer vehicleCapacity,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWichBoardsInNode,
            Map<Integer, List<Request>> requestsWichLeavesInNode, Integer numberOfNodes,
            Set<Integer> setOfVehicles, List<Request> listOfNonAttendedRequests, List<Request> requestList,
            List<Integer> loadIndexList, List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes,
            Long timeWindows, Long currentTime, Integer lastNode) throws FileNotFoundException {

        Solution solution = greedyConstructive(0.2, 0.15, 0.55, 0.1, listOfRequests, requestsWichBoardsInNode,
                requestsWichLeavesInNode, numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList,
                loadIndexList, timeBetweenNodes, distanceBetweenNodes, timeWindows, currentTime, lastNode);

        Solution solution1 = new Solution();
        String folder = "RandomSolutionsUsingPerturbation";
        boolean success = (new File(folder)).mkdirs();
        String destinationFileForObjectives = folder + "/Random_Solutions_" + numberOfRandomSolutions + "_Objectives.txt";
        String destinationFileForSolutions = folder + "/Random_Solutions_" + numberOfRandomSolutions + "_Solutions.txt";

        PrintStream printStreamForObjectives = new PrintStream(destinationFileForObjectives);
        PrintStream printStreamForSolutions = new PrintStream(destinationFileForSolutions);
        for (int i = 0; i < numberOfRandomSolutions; i++) {
            solution1.setSolution(perturbation(solution, listOfRequests, requestsWichBoardsInNode, requestsWichLeavesInNode,
                    numberOfNodes, vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes,
                    distanceBetweenNodes, timeWindows));
            //System.out.println(solution1.getStringWithObjectives());
            System.out.println(solution1);
            printStreamForObjectives.print(solution1.getStringWithObjectives() + "\n");
            printStreamForSolutions.print(solution1 + "\n");
        }
    }

    public static void generateRandomSolutionsUsingGreedyAlgorithm(int numberOfRandomSolutions, final Integer vehicleCapacity,
            List<Request> listOfRequests, Map<Integer, List<Request>> requestsWichBoardsInNode,
            Map<Integer, List<Request>> requestsWichLeavesInNode, Integer numberOfNodes, Set<Integer> setOfVehicles,
            List<Request> listOfNonAttendedRequests, List<Request> requestList, List<Integer> loadIndexList,
            List<List<Long>> timeBetweenNodes, List<List<Long>> distanceBetweenNodes, Long timeWindows,
            Long currentTime, Integer lastNode) throws FileNotFoundException {
        Solution solution1 = new Solution();
        String folder = "RandomSolutionsUsingGreedyAlgorithm";
        boolean success = (new File(folder)).mkdirs();
        if (!success) {
            System.out.println("Folder already exist!");
        }
        String destinationFileForObjectives = folder + "/Random_Solutions_" + numberOfRandomSolutions + "_Objectives.txt";
        String destinationFileForSolutions = folder + "/Random_Solutions_" + numberOfRandomSolutions + "_Solutions.txt";
        PrintStream printStreamForObjectives = new PrintStream(destinationFileForObjectives);
        PrintStream printStreamForSolutions = new PrintStream(destinationFileForSolutions);
        for (int i = 0; i < numberOfRandomSolutions; i++) {
            solution1.setSolution(geraPesos(i, listOfRequests, requestsWichBoardsInNode, requestsWichLeavesInNode, numberOfNodes,
                    vehicleCapacity, setOfVehicles, listOfNonAttendedRequests, requestList, loadIndexList, timeBetweenNodes,
                    distanceBetweenNodes, timeWindows, currentTime, lastNode));
            System.out.println(solution1.getStringWithObjectives());
            printStreamForObjectives.print(solution1.getStringWithObjectives() + "\n");
            printStreamForSolutions.print(solution1 + "\n");
        }
    }

    public static void printProblemInformations(List<Request> listOfRequests, final Integer numberOfVehicles, final Integer vehicleCapacity, String instanceName, String adjacenciesData, String nodesData) {
        System.out.println("VRPDRT - Informations");
        System.out.println("Number of requests = " + listOfRequests.size());
        System.out.println("Number of vehicles = " + numberOfVehicles);
        System.out.println("Vehicle capacity = " + vehicleCapacity);
        System.out.println("Instance name = " + instanceName);
        System.out.println("Adjacencies instance = " + adjacenciesData);
        System.out.println("Nodes instance = " + nodesData);
        System.out.println();
    }
    
    public static void testMethod(){
        System.out.println();
    }

}
