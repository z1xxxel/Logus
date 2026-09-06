package com.logus.tms_backend.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CityCoordinatesService {

    // Координаты городов (широта, долгота)
    private static final Map<String, double[]> CITY_COORDINATES = new HashMap<>();

    static {
        CITY_COORDINATES.put("москва", new double[]{55.7558, 37.6173});
        CITY_COORDINATES.put("санкт-петербург", new double[]{59.9343, 30.3351});
        CITY_COORDINATES.put("нижний новгород", new double[]{56.2965, 43.9361});
        CITY_COORDINATES.put("казань", new double[]{55.8304, 49.0661});
        CITY_COORDINATES.put("самара", new double[]{53.2001, 50.1500});
        CITY_COORDINATES.put("саратов", new double[]{51.5924, 46.0348});
        CITY_COORDINATES.put("воронеж", new double[]{51.6720, 39.1843});
        CITY_COORDINATES.put("тула", new double[]{54.1961, 37.6182});
        CITY_COORDINATES.put("ярославль", new double[]{57.6261, 39.8845});
        CITY_COORDINATES.put("владимир", new double[]{56.1366, 40.3966});
        CITY_COORDINATES.put("брянск", new double[]{53.2434, 34.3656});
        CITY_COORDINATES.put("калуга", new double[]{54.5293, 36.2754});
        CITY_COORDINATES.put("курск", new double[]{51.7373, 36.1873});
        CITY_COORDINATES.put("орел", new double[]{52.9651, 36.0785});
        CITY_COORDINATES.put("тамбов", new double[]{52.7319, 41.4520});
        CITY_COORDINATES.put("липецк", new double[]{52.6031, 39.5708});
        CITY_COORDINATES.put("белгород", new double[]{50.5951, 36.5879});

        // Юг России
        CITY_COORDINATES.put("ростов-на-дону", new double[]{47.2357, 39.7015});
        CITY_COORDINATES.put("краснодар", new double[]{45.0355, 38.9753});
        CITY_COORDINATES.put("сочи", new double[]{43.6028, 39.7342});
        CITY_COORDINATES.put("волгоград", new double[]{48.7080, 44.5133});
        CITY_COORDINATES.put("астрахань", new double[]{46.3497, 48.0408});
        CITY_COORDINATES.put("новороссийск", new double[]{44.7230, 37.7688});
        CITY_COORDINATES.put("ставрополь", new double[]{45.0446, 41.9691});
        CITY_COORDINATES.put("махачкала", new double[]{42.9841, 47.5047});

        // Поволжье
        CITY_COORDINATES.put("уфа", new double[]{54.7388, 55.9721});
        CITY_COORDINATES.put("пермь", new double[]{58.0105, 56.2502});
        CITY_COORDINATES.put("киров", new double[]{58.6035, 49.6680});
        CITY_COORDINATES.put("нижний тагил", new double[]{57.9197, 59.9647});
        CITY_COORDINATES.put("набережные челны", new double[]{55.7251, 52.4069});
        CITY_COORDINATES.put("ульяновск", new double[]{54.3142, 48.4031});
        CITY_COORDINATES.put("пенза", new double[]{53.2007, 45.0046});
        CITY_COORDINATES.put("тольятти", new double[]{53.5303, 49.3461});

        // Урал
        CITY_COORDINATES.put("екатеринбург", new double[]{56.8389, 60.6057});
        CITY_COORDINATES.put("челябинск", new double[]{55.1644, 61.4368});
        CITY_COORDINATES.put("тюмень", new double[]{57.1522, 65.5272});
        CITY_COORDINATES.put("омск", new double[]{54.9884, 73.3242});
        CITY_COORDINATES.put("курган", new double[]{55.4500, 65.3333});
        CITY_COORDINATES.put("сургут", new double[]{61.2500, 73.4167});
        CITY_COORDINATES.put("нижневартовск", new double[]{60.9344, 76.5531});

        // Сибирь
        CITY_COORDINATES.put("новосибирск", new double[]{55.0084, 82.9357});
        CITY_COORDINATES.put("томск", new double[]{56.4977, 84.9744});
        CITY_COORDINATES.put("омск", new double[]{54.9884, 73.3242});
        CITY_COORDINATES.put("кемерово", new double[]{55.3331, 86.0831});
        CITY_COORDINATES.put("новокузнецк", new double[]{53.7596, 87.1264});
        CITY_COORDINATES.put("красноярск", new double[]{56.0153, 92.8932});
        CITY_COORDINATES.put("иркутск", new double[]{52.2978, 104.2964});
        CITY_COORDINATES.put("барнаул", new double[]{53.3606, 83.7636});
        CITY_COORDINATES.put("чита", new double[]{52.0297, 113.4822});
        CITY_COORDINATES.put("якутск", new double[]{62.0397, 129.7322});

        // Дальний Восток
        CITY_COORDINATES.put("владивосток", new double[]{43.1056, 131.8735});
        CITY_COORDINATES.put("хабаровск", new double[]{48.4827, 135.0840});
        CITY_COORDINATES.put("благовещенск", new double[]{50.2672, 127.5408});
        CITY_COORDINATES.put("южно-сахалинск", new double[]{46.9588, 142.7386});
        CITY_COORDINATES.put("петропавловск-камчатский", new double[]{53.0446, 158.6510});
        CITY_COORDINATES.put("магадан", new double[]{59.5684, 150.8022});

        // Север
        CITY_COORDINATES.put("мурманск", new double[]{68.9585, 33.0827});
        CITY_COORDINATES.put("архангельск", new double[]{64.5401, 40.5433});
        CITY_COORDINATES.put("петрозаводск", new double[]{61.7849, 34.3469});
        CITY_COORDINATES.put("сыктывкар", new double[]{61.6681, 50.8251});
        CITY_COORDINATES.put("воркута", new double[]{67.4958, 64.0569});

        // Калининград
        CITY_COORDINATES.put("калининград", new double[]{54.7104, 20.4522});
        CITY_COORDINATES.put("рязань", new double[]{53.7452, 39.6999});
        CITY_COORDINATES.put("смоленск", new double[]{54.7818, 32.0401});
        CITY_COORDINATES.put("тверь", new double[]{56.8587, 35.9177});
        CITY_COORDINATES.put("иваново", new double[]{57.0004, 40.9739});
        CITY_COORDINATES.put("кострома", new double[]{57.7679, 40.9268});
        CITY_COORDINATES.put("владикавказ", new double[]{43.0241, 44.6904});
        CITY_COORDINATES.put("нальчик", new double[]{43.4981, 43.6189});
        CITY_COORDINATES.put("черкесск", new double[]{44.2266, 42.0483});
        CITY_COORDINATES.put("майкоп", new double[]{44.6098, 40.1006});
        CITY_COORDINATES.put("элиста", new double[]{46.3078, 44.2558});
        CITY_COORDINATES.put("саранск", new double[]{54.1874, 45.1839});
        CITY_COORDINATES.put("чебоксары", new double[]{56.1322, 47.2506});
        CITY_COORDINATES.put("йошкар-ола", new double[]{56.6329, 47.8860});
        CITY_COORDINATES.put("ижевск", new double[]{56.8527, 53.2115});
        CITY_COORDINATES.put("оренбург", new double[]{51.7727, 55.0988});

        // Промышленные и портовые города
        CITY_COORDINATES.put("магнитогорск", new double[]{53.4186, 59.0291});
        CITY_COORDINATES.put("норильск", new double[]{69.3558, 88.1893});
        CITY_COORDINATES.put("мурманск", new double[]{68.9585, 33.0827});
        CITY_COORDINATES.put("архангельск", new double[]{64.5401, 40.5433});
        CITY_COORDINATES.put("калининград", new double[]{54.7104, 20.4522});
        CITY_COORDINATES.put("петрозаводск", new double[]{61.7849, 34.3469});
        CITY_COORDINATES.put("великий новгород", new double[]{58.5228, 31.2718});
        CITY_COORDINATES.put("псков", new double[]{57.8194, 28.3318});
        CITY_COORDINATES.put("вологда", new double[]{59.2239, 39.8837});
        CITY_COORDINATES.put("череповец", new double[]{59.1300, 37.9158});
        CITY_COORDINATES.put("сыктывкар", new double[]{61.6681, 50.8251});
        CITY_COORDINATES.put("нарьян-мар", new double[]{67.6380, 53.0069});
        CITY_COORDINATES.put("салехард", new double[]{66.5300, 66.6019});
        CITY_COORDINATES.put("ханты-мансийск", new double[]{61.0042, 69.0019});

        // Дополнительные города Поволжья и Урала
        CITY_COORDINATES.put("стерлитамак", new double[]{53.6308, 55.9481});
        CITY_COORDINATES.put("орск", new double[]{51.2049, 58.5668});
        CITY_COORDINATES.put("альметьевск", new double[]{54.8931, 52.3173});
        CITY_COORDINATES.put("дзержинск", new double[]{56.2399, 43.4553});
        CITY_COORDINATES.put("арзамас", new double[]{55.3889, 43.8097});
        CITY_COORDINATES.put("саров", new double[]{54.9386, 43.3235});
        CITY_COORDINATES.put("златоуст", new double[]{55.1711, 59.6508});
        CITY_COORDINATES.put("миасс", new double[]{55.0461, 60.1183});
        CITY_COORDINATES.put("копейск", new double[]{55.1144, 61.6194});
        CITY_COORDINATES.put("каменск-уральский", new double[]{56.4019, 61.9319});
        CITY_COORDINATES.put("первоуральск", new double[]{56.9058, 59.9425});

        // Дополнительные города Сибири и Дальнего Востока
        CITY_COORDINATES.put("братск", new double[]{56.1333, 101.6167});
        CITY_COORDINATES.put("ангарск", new double[]{52.5417, 103.8889});
        CITY_COORDINATES.put("усолье-сибирское", new double[]{52.6500, 103.6333});
        CITY_COORDINATES.put("норильск", new double[]{69.3558, 88.1893});
        CITY_COORDINATES.put("абакан", new double[]{53.7167, 91.4333});
        CITY_COORDINATES.put("кызыл", new double[]{51.7167, 94.4500});
        CITY_COORDINATES.put("горно-алтайск", new double[]{51.9583, 85.9611});
        CITY_COORDINATES.put("улан-удэ", new double[]{51.8333, 107.5833});
        CITY_COORDINATES.put("комсомольск-на-амуре", new double[]{50.5500, 137.0000});
        CITY_COORDINATES.put("уссурийск", new double[]{43.8000, 131.9500});
        CITY_COORDINATES.put("находка", new double[]{42.8333, 132.8833});
        CITY_COORDINATES.put("артём", new double[]{43.3500, 132.1833});
        CITY_COORDINATES.put("магадан", new double[]{59.5684, 150.8022});
        CITY_COORDINATES.put("якутск", new double[]{62.0397, 129.7322});
        CITY_COORDINATES.put("нерюнгри", new double[]{56.6667, 124.7333});
        CITY_COORDINATES.put("мирный", new double[]{62.5333, 114.0000});

        // Золотое кольцо и исторические города
        CITY_COORDINATES.put("суздаль", new double[]{56.4211, 40.4489});
        CITY_COORDINATES.put("владимир", new double[]{56.1366, 40.3966});
        CITY_COORDINATES.put("сергиев посад", new double[]{56.3153, 38.1353});
        CITY_COORDINATES.put("переславль-залесский", new double[]{56.7381, 38.8561});
        CITY_COORDINATES.put("ростов великий", new double[]{57.1953, 39.4153});
        CITY_COORDINATES.put("углич", new double[]{57.5233, 38.3067});
        CITY_COORDINATES.put("мышкин", new double[]{57.8333, 38.4500});
        CITY_COORDINATES.put("палех", new double[]{56.7833, 41.8500});

        // Дополнительные города Северного Кавказа
        CITY_COORDINATES.put("грозный", new double[]{43.3167, 45.7000});
        CITY_COORDINATES.put("махачкала", new double[]{42.9841, 47.5047});
        CITY_COORDINATES.put("хасавюрт", new double[]{43.2500, 46.5833});
        CITY_COORDINATES.put("дербент", new double[]{42.0667, 48.2833});
        CITY_COORDINATES.put("кызляр", new double[]{43.8500, 46.7167});
        CITY_COORDINATES.put("будённовск", new double[]{44.7833, 44.1500});
        CITY_COORDINATES.put("нефтекумск", new double[]{44.7000, 44.9833});
        CITY_COORDINATES.put("георгиевск", new double[]{44.1500, 43.4667});
        CITY_COORDINATES.put("минеральные воды", new double[]{44.2000, 43.1167});
        CITY_COORDINATES.put("кисловодск", new double[]{43.9000, 42.7167});
        CITY_COORDINATES.put("пятигорск", new double[]{44.0500, 43.0500});
        CITY_COORDINATES.put("ессентуки", new double[]{44.0333, 42.8667});
        CITY_COORDINATES.put("железноводск", new double[]{44.1333, 43.0167});

        // Дополнительные города Крыма и Черноморского побережья
        CITY_COORDINATES.put("севастополь", new double[]{44.6167, 33.5333});
        CITY_COORDINATES.put("симферополь", new double[]{44.9500, 34.1000});
        CITY_COORDINATES.put("керчь", new double[]{45.3500, 36.4667});
        CITY_COORDINATES.put("евпатория", new double[]{45.2000, 33.3667});
        CITY_COORDINATES.put("феодосия", new double[]{45.0333, 35.3833});
        CITY_COORDINATES.put("ялта", new double[]{44.5000, 34.1667});
        CITY_COORDINATES.put("анапа", new double[]{44.9000, 37.3167});
        CITY_COORDINATES.put("геленджик", new double[]{44.5667, 38.0833});
        CITY_COORDINATES.put("туапсе", new double[]{44.1000, 39.0667});

        // === ДОПОЛНИТЕЛЬНЫЕ ГОРОДА СНГ ===

        // Беларусь
        CITY_COORDINATES.put("гомель", new double[]{52.4333, 31.0000});
        CITY_COORDINATES.put("могилёв", new double[]{53.9000, 30.3333});
        CITY_COORDINATES.put("витебск", new double[]{55.1833, 30.2000});
        CITY_COORDINATES.put("гродно", new double[]{53.6833, 23.8333});
        CITY_COORDINATES.put("брест", new double[]{52.1000, 23.7500});
        // === ДОПОЛНИТЕЛЬНЫЕ ГОРОДА РОССИИ ===

        // === Центральная Россия и Подмосковье ===
        CITY_COORDINATES.put("подольск", new double[]{55.4309, 37.5456});
        CITY_COORDINATES.put("балашниха", new double[]{55.7961, 37.9539});
        CITY_COORDINATES.put("химки", new double[]{55.8970, 37.4296});
        CITY_COORDINATES.put("мытищи", new double[]{55.9116, 37.7308});
        CITY_COORDINATES.put("люберцы", new double[]{55.6768, 37.8938});
        CITY_COORDINATES.put("королёв", new double[]{55.9161, 37.8539});
        CITY_COORDINATES.put("красногорск", new double[]{55.8333, 37.3333});
        CITY_COORDINATES.put("одинцово", new double[]{55.6781, 37.2753});
        CITY_COORDINATES.put("коломна", new double[]{55.0789, 38.7669});
        CITY_COORDINATES.put("серпухов", new double[]{54.9167, 37.4167});
        CITY_COORDINATES.put("ногинск", new double[]{55.8556, 38.4458});
        CITY_COORDINATES.put("щулково", new double[]{55.9583, 38.1917});
        CITY_COORDINATES.put("домодедово", new double[]{55.3889, 37.7833});
        CITY_COORDINATES.put("жуковский", new double[]{55.5953, 38.1206});
        CITY_COORDINATES.put("раменское", new double[]{55.5833, 38.2333});
        CITY_COORDINATES.put("электросталь", new double[]{55.7833, 38.4333});
        CITY_COORDINATES.put("орехово-зуево", new double[]{55.8167, 38.9500});
        CITY_COORDINATES.put("клин", new double[]{56.3333, 36.7333});
        CITY_COORDINATES.put("дмитров", new double[]{56.3500, 37.5167});
        CITY_COORDINATES.put("чехов", new double[]{55.1500, 37.4667});
        CITY_COORDINATES.put("стипино", new double[]{54.8333, 37.5500});
        CITY_COORDINATES.put("наро-фоминск", new double[]{55.3833, 36.7333});
        CITY_COORDINATES.put("воскресенск", new double[]{55.3167, 38.6833});

        // === Города Золотого кольца и древние города ===
        CITY_COORDINATES.put("суздаль", new double[]{56.4211, 40.4489});
        CITY_COORDINATES.put("сергиев посад", new double[]{56.3153, 38.1353});
        CITY_COORDINATES.put("переславль-залесский", new double[]{56.7381, 38.8561});
        CITY_COORDINATES.put("ростов великий", new double[]{57.1953, 39.4153});
        CITY_COORDINATES.put("углич", new double[]{57.5233, 38.3067});
        CITY_COORDINATES.put("мышкин", new double[]{57.8333, 38.4500});
        CITY_COORDINATES.put("палех", new double[]{56.7833, 41.8500});
        CITY_COORDINATES.put("плёс", new double[]{57.4500, 41.5167});
        CITY_COORDINATES.put("мстера", new double[]{56.5833, 42.1500});
        CITY_COORDINATES.put("гусь-хрустальный", new double[]{55.6167, 40.6500});
        CITY_COORDINATES.put("шуя", new double[]{56.8500, 41.3667});
        CITY_COORDINATES.put("киржач", new double[]{56.2167, 38.8667});
        CITY_COORDINATES.put("александров", new double[]{56.4000, 38.7333});
        CITY_COORDINATES.put("калязин", new double[]{57.2500, 37.8333});
        CITY_COORDINATES.put("бежецк", new double[]{57.7833, 36.7000});
        CITY_COORDINATES.put("торжок", new double[]{57.0333, 34.9667});
        CITY_COORDINATES.put("вышний волочёк", new double[]{57.1167, 35.5333});
        CITY_COORDINATES.put("ржев", new double[]{56.2667, 34.3333});
        CITY_COORDINATES.put("вязьма", new double[]{55.2167, 34.3000});
        CITY_COORDINATES.put("дорогобуж", new double[]{54.9167, 33.3000});

        // === Северо-Запад России ===
        CITY_COORDINATES.put("великий новгород", new double[]{58.5228, 31.2718});
        CITY_COORDINATES.put("псков", new double[]{57.8194, 28.3318});
        CITY_COORDINATES.put("вологда", new double[]{59.2239, 39.8837});
        CITY_COORDINATES.put("череповец", new double[]{59.1300, 37.9158});
        CITY_COORDINATES.put("старая русса", new double[]{57.9833, 31.3500});
        CITY_COORDINATES.put("боровичи", new double[]{58.3833, 33.9167});
        CITY_COORDINATES.put("кириши", new double[]{59.5167, 32.0167});
        CITY_COORDINATES.put("тихвин", new double[]{59.6333, 33.5167});
        CITY_COORDINATES.put("лодейное поле", new double[]{60.7333, 33.5500});
        CITY_COORDINATES.put("подпорожье", new double[]{60.9000, 34.1667});
        CITY_COORDINATES.put("вытегра", new double[]{61.0000, 36.4500});
        CITY_COORDINATES.put("каргополь", new double[]{61.5000, 38.9333});
        CITY_COORDINATES.put("котлас", new double[]{61.2500, 46.6500});
        CITY_COORDINATES.put("северодвинск", new double[]{64.5667, 39.8167});
        CITY_COORDINATES.put("новодвинск", new double[]{64.4167, 40.8167});
        CITY_COORDINATES.put("она", new double[]{64.5333, 40.5333});
        CITY_COORDINATES.put("мирный", new double[]{62.5333, 40.4000});
        CITY_COORDINATES.put("коноша", new double[]{60.9667, 39.8333});
        CITY_COORDINATES.put("вельск", new double[]{61.0667, 42.1000});

        // === Калининградская область ===
        CITY_COORDINATES.put("калининград", new double[]{54.7104, 20.4522});
        CITY_COORDINATES.put("советск", new double[]{55.0833, 21.8833});
        CITY_COORDINATES.put("черняховск", new double[]{54.6333, 21.8167});
        CITY_COORDINATES.put("гусев", new double[]{54.5833, 22.2000});
        CITY_COORDINATES.put("балтийск", new double[]{54.6500, 19.9500});
        CITY_COORDINATES.put("светлогорск", new double[]{54.9333, 20.0667});
        CITY_COORDINATES.put("зеленоградск", new double[]{54.9667, 20.4833});
        CITY_COORDINATES.put("пионерский", new double[]{54.9500, 20.2333});
        CITY_COORDINATES.put("полесск", new double[]{54.8500, 21.1000});
        CITY_COORDINATES.put("гвардейск", new double[]{54.6500, 21.0500});
        CITY_COORDINATES.put("неман", new double[]{55.0333, 22.0333});
        CITY_COORDINATES.put("краснознаменск", new double[]{54.8833, 22.5000});
        CITY_COORDINATES.put("озёрск", new double[]{54.5833, 22.0167});
        CITY_COORDINATES.put("правдинск", new double[]{54.4333, 21.0167});
        CITY_COORDINATES.put("багратионовск", new double[]{54.3833, 20.6333});
        CITY_COORDINATES.put("мамоново", new double[]{54.4667, 20.1833});
        CITY_COORDINATES.put("ладушкин", new double[]{54.5667, 20.1667});
        CITY_COORDINATES.put("светлый", new double[]{54.6667, 20.1333});

        // === Юг России и Северный Кавказ ===
        CITY_COORDINATES.put("таганрог", new double[]{47.2333, 38.9000});
        CITY_COORDINATES.put("шахты", new double[]{47.7000, 40.2167});
        CITY_COORDINATES.put("новочеркасск", new double[]{47.4167, 40.1000});
        CITY_COORDINATES.put("каменск-шахтинский", new double[]{48.3167, 40.2500});
        CITY_COORDINATES.put("азов", new double[]{47.1000, 39.4167});
        CITY_COORDINATES.put("белая калитва", new double[]{48.1833, 40.7833});
        CITY_COORDINATES.put("красный сулин", new double[]{48.0833, 40.5333});
        CITY_COORDINATES.put("волгодонск", new double[]{47.5167, 42.1667});
        CITY_COORDINATES.put("салск", new double[]{46.4833, 41.5333});
        CITY_COORDINATES.put("батайск", new double[]{47.1333, 39.7500});
        CITY_COORDINATES.put("армавир", new double[]{45.0000, 41.1333});
        CITY_COORDINATES.put("ейск", new double[]{46.7167, 38.2667});
        CITY_COORDINATES.put("тихорецк", new double[]{45.8500, 40.1333});
        CITY_COORDINATES.put("кропоткин", new double[]{45.4333, 40.5833});
        CITY_COORDINATES.put("белореченск", new double[]{44.7667, 39.8667});
        CITY_COORDINATES.put("апшеронск", new double[]{44.4500, 39.7333});
        CITY_COORDINATES.put("хадыженск", new double[]{44.4167, 39.5333});
        CITY_COORDINATES.put("лабинск", new double[]{44.6333, 41.0500});
        CITY_COORDINATES.put("отрадная", new double[]{44.4000, 41.0500});
        CITY_COORDINATES.put("усть-лабинск", new double[]{45.2167, 39.6833});
        CITY_COORDINATES.put("темрюк", new double[]{45.2667, 37.3833});
        CITY_COORDINATES.put("славянск-на-кубани", new double[]{45.2167, 38.1333});
        CITY_COORDINATES.put("приморско-ахтарск", new double[]{46.0500, 38.1667});
        CITY_COORDINATES.put("тимашёвск", new double[]{45.6167, 38.9500});
        CITY_COORDINATES.put("кореновск", new double[]{45.4667, 39.4500});
        CITY_COORDINATES.put("крымск", new double[]{44.9333, 37.9833});
        CITY_COORDINATES.put("абinsk", new double[]{44.8667, 38.3667});

        // === Поволжье (дополнительно) ===
        CITY_COORDINATES.put("волжский", new double[]{48.8000, 44.7667});
        CITY_COORDINATES.put("камьшин", new double[]{50.0833, 45.4000});
        CITY_COORDINATES.put("михайловка", new double[]{50.0667, 43.2333});
        CITY_COORDINATES.put("фролово", new double[]{50.4833, 43.6500});
        CITY_COORDINATES.put("урух", new double[]{48.9000, 44.5000});
        CITY_COORDINATES.put("элиста", new double[]{46.3078, 44.2558});
        CITY_COORDINATES.put("знаменск", new double[]{46.4000, 47.3667});
        CITY_COORDINATES.put("ахтубинск", new double[]{47.4167, 47.0000});
        CITY_COORDINATES.put("харабали", new double[]{47.4000, 47.2500});
        CITY_COORDINATES.put("нариманов", new double[]{46.6833, 47.8500});
        CITY_COORDINATES.put("красноармейск", new double[]{50.0667, 45.6167});
        CITY_COORDINATES.put("палласовка", new double[]{50.0500, 46.8833});
        CITY_COORDINATES.put("ленинск", new double[]{48.7000, 45.2000});
        CITY_COORDINATES.put("котельниково", new double[]{47.6333, 43.1333});
        CITY_COORDINATES.put("светлый яр", new double[]{48.3000, 44.7000});
        CITY_COORDINATES.put("суровикино", new double[]{48.6167, 42.8500});
        CITY_COORDINATES.put("каalach", new double[]{50.4333, 41.1833});

        // === Урал (дополнительно) ===
        CITY_COORDINATES.put("магнитогорск", new double[]{53.4186, 59.0291});
        CITY_COORDINATES.put("златоуст", new double[]{55.1711, 59.6508});
        CITY_COORDINATES.put("миасс", new double[]{55.0461, 60.1183});
        CITY_COORDINATES.put("копейск", new double[]{55.1144, 61.6194});
        CITY_COORDINATES.put("каменск-уральский", new double[]{56.4019, 61.9319});
        CITY_COORDINATES.put("первоуральск", new double[]{56.9058, 59.9425});
        CITY_COORDINATES.put("ревда", new double[]{56.8000, 59.9167});
        CITY_COORDINATES.put("нижняя салда", new double[]{58.0500, 59.8167});
        CITY_COORDINATES.put("верхняя пышма", new double[]{56.9667, 60.5833});
        CITY_COORDINATES.put("берёзовский", new double[]{56.9167, 60.8000});
        CITY_COORDINATES.put("асбест", new double[]{57.0000, 61.4500});
        CITY_COORDINATES.put("полевской", new double[]{56.5000, 60.4000});
        CITY_COORDINATES.put("сысерть", new double[]{56.5167, 60.8167});
        CITY_COORDINATES.put("дегтярск", new double[]{56.7000, 60.0833});
        CITY_COORDINATES.put("киштым", new double[]{55.7500, 60.5500});
        CITY_COORDINATES.put("озёрск", new double[]{55.7500, 60.7167});
        CITY_COORDINATES.put("снежинск", new double[]{56.0833, 60.7333});
        CITY_COORDINATES.put("трёхгорный", new double[]{54.7667, 58.4500});
        CITY_COORDINATES.put("сатка", new double[]{55.0833, 59.0333});
        CITY_COORDINATES.put("катав-ивановск", new double[]{54.8333, 58.2000});
        CITY_COORDINATES.put("южноуральск", new double[]{54.4500, 61.2500});
        CITY_COORDINATES.put("троицк", new double[]{54.0833, 61.5667});
        CITY_COORDINATES.put("верхний уфалей", new double[]{56.0500, 60.0833});

        // === Сибирь (дополнительно) ===
        CITY_COORDINATES.put("братск", new double[]{56.1333, 101.6167});
        CITY_COORDINATES.put("ангарск", new double[]{52.5417, 103.8889});
        CITY_COORDINATES.put("усолье-сибирское", new double[]{52.6500, 103.6333});
        CITY_COORDINATES.put("черемхово", new double[]{53.1333, 103.0667});
        CITY_COORDINATES.put("усолье", new double[]{59.4167, 56.6833});
        CITY_COORDINATES.put("абакан", new double[]{53.7167, 91.4333});
        CITY_COORDINATES.put("кызыл", new double[]{51.7167, 94.4500});
        CITY_COORDINATES.put("горно-алтайск", new double[]{51.9583, 85.9611});
        CITY_COORDINATES.put("улан-удэ", new double[]{51.8333, 107.5833});
        CITY_COORDINATES.put("северобайкальск", new double[]{55.6333, 109.3333});
        CITY_COORDINATES.put("тулун", new double[]{54.5500, 100.5667});
        CITY_COORDINATES.put("тайшет", new double[]{55.9333, 98.0000});
        CITY_COORDINATES.put("нижнеудинск", new double[]{54.9000, 99.0333});
        CITY_COORDINATES.put("бодайбо", new double[]{56.1333, 114.2000});
        CITY_COORDINATES.put("усть-кут", new double[]{56.8000, 105.7667});
        CITY_COORDINATES.put("киренск", new double[]{57.7667, 108.0667});
        CITY_COORDINATES.put("всихолох", new double[]{60.0833, 116.3000});
        CITY_COORDINATES.put("айхал", new double[]{65.9500, 111.5333});
        CITY_COORDINATES.put("удачный", new double[]{66.4000, 112.3000});
        CITY_COORDINATES.put("покровск", new double[]{61.4833, 129.1333});
        CITY_COORDINATES.put("алдан", new double[]{58.8500, 125.4167});
        CITY_COORDINATES.put("томмот", new double[]{58.6167, 126.8333});
        CITY_COORDINATES.put("ньурба", new double[]{63.3000, 118.3333});
        CITY_COORDINATES.put("вилюйск", new double[]{63.7500, 121.6000});
        CITY_COORDINATES.put("среднеколымск", new double[]{67.4500, 153.7333});

        // === Дальний Восток (дополнительно) ===
        CITY_COORDINATES.put("комсомольск-на-амуре", new double[]{50.5500, 137.0000});
        CITY_COORDINATES.put("уссурийск", new double[]{43.8000, 131.9500});
        CITY_COORDINATES.put("находка", new double[]{42.8333, 132.8833});
        CITY_COORDINATES.put("артём", new double[]{43.3500, 132.1833});
        CITY_COORDINATES.put("арсеньев", new double[]{44.1667, 133.2667});
        CITY_COORDINATES.put("спасск-дальний", new double[]{44.6000, 132.8167});
        CITY_COORDINATES.put("лесозаводск", new double[]{45.4833, 133.3833});
        CITY_COORDINATES.put("большой камень", new double[]{43.1167, 132.3500});
        CITY_COORDINATES.put("партызанск", new double[]{42.8833, 133.0833});
        CITY_COORDINATES.put("фокино", new double[]{42.9667, 132.4167});
        CITY_COORDINATES.put("дачнегорск", new double[]{44.5500, 135.5667});
        CITY_COORDINATES.put("кавалерово", new double[]{44.2667, 135.0333});
        CITY_COORDINATES.put(" Olga", new double[]{44.9333, 135.9000});
        CITY_COORDINATES.put("пластуны", new double[]{44.7667, 136.3000});
        CITY_COORDINATES.put("терней", new double[]{45.0500, 136.7333});
        CITY_COORDINATES.put("советская гавань", new double[]{48.9667, 140.2833});
        CITY_COORDINATES.put("ванино", new double[]{49.0833, 140.2500});
        CITY_COORDINATES.put("бикоан", new double[]{49.0167, 140.0500});
        CITY_COORDINATES.put(" аян", new double[]{59.2333, 143.3500});
        CITY_COORDINATES.put("николаевск-на-амуре", new double[]{53.1500, 140.7333});
        CITY_COORDINATES.put("амурск", new double[]{50.2333, 136.9000});

        // === Север Европейской части ===
        CITY_COORDINATES.put("апатиты", new double[]{67.5667, 33.4000});
        CITY_COORDINATES.put("кировск", new double[]{67.6167, 33.6833});
        CITY_COORDINATES.put("мончегорск", new double[]{67.5833, 32.8833});
        CITY_COORDINATES.put("полярные зори", new double[]{66.9500, 32.5000});
        CITY_COORDINATES.put("кандалакша", new double[]{67.1500, 32.4000});
        CITY_COORDINATES.put("ковдор", new double[]{67.5667, 30.4833});
        CITY_COORDINATES.put("заполярный", new double[]{69.4167, 32.4500});
        CITY_COORDINATES.put("североморск", new double[]{69.0667, 33.4167});
        CITY_COORDINATES.put("гadzиеvo", new double[]{69.2500, 33.3167});
        CITY_COORDINATES.put("видяево", new double[]{69.1333, 33.0667});
        CITY_COORDINATES.put("полярный", new double[]{69.2000, 33.4500});
        CITY_COORDINATES.put("оленегорск", new double[]{68.1333, 33.2833});

        // === Белоруссия ===
        CITY_COORDINATES.put("минск", new double[]{53.9006, 27.5590});
        CITY_COORDINATES.put("гомель", new double[]{52.4333, 31.0000});
        CITY_COORDINATES.put("могилёв", new double[]{53.9000, 30.3333});
        CITY_COORDINATES.put("витебск", new double[]{55.1833, 30.2000});
        CITY_COORDINATES.put("гродно", new double[]{53.6833, 23.8333});
        CITY_COORDINATES.put("брест", new double[]{52.1000, 23.7500});
        CITY_COORDINATES.put("бобруйск", new double[]{53.1333, 29.2167});
        CITY_COORDINATES.put("барановичи", new double[]{53.1333, 26.0167});
        CITY_COORDINATES.put("борисов", new double[]{54.2333, 28.5000});
        CITY_COORDINATES.put("пинск", new double[]{52.1167, 26.0833});
        CITY_COORDINATES.put("орша", new double[]{54.5167, 30.4167});
        CITY_COORDINATES.put("мозырь", new double[]{52.0500, 29.2500});
        CITY_COORDINATES.put("солигорск", new double[]{52.7833, 27.5333});
        CITY_COORDINATES.put("новополоцк", new double[]{55.5333, 28.6667});
        CITY_COORDINATES.put("лида", new double[]{53.8833, 25.3000});
        CITY_COORDINATES.put("молодечно", new double[]{54.3167, 26.8500});
        CITY_COORDINATES.put("полоцк", new double[]{55.4833, 28.8000});
        CITY_COORDINATES.put("светлогорск", new double[]{52.6333, 29.7333});
        CITY_COORDINATES.put("слоним", new double[]{53.0833, 25.3167});
        CITY_COORDINATES.put("волковыск", new double[]{53.1667, 24.4333});
        CITY_COORDINATES.put(" Kobrin", new double[]{52.2167, 24.3500});
        CITY_COORDINATES.put("иваново", new double[]{52.1167, 25.5333});
        CITY_COORDINATES.put("берёза", new double[]{52.5333, 24.9833});
        CITY_COORDINATES.put("лунинец", new double[]{52.2500, 26.8000});
        CITY_COORDINATES.put("столин", new double[]{51.9000, 26.8333});
        CITY_COORDINATES.put("глубокое", new double[]{55.1333, 27.7000});
        CITY_COORDINATES.put("поставы", new double[]{55.1167, 26.8333});
        CITY_COORDINATES.put("сморгонь", new double[]{54.4833, 26.4000});
        CITY_COORDINATES.put("осиповичи", new double[]{53.3000, 28.6333});
        CITY_COORDINATES.put("кричев", new double[]{53.7167, 31.7167});
        CITY_COORDINATES.put("шклов", new double[]{54.2167, 30.2833});
        CITY_COORDINATES.put("чаусы", new double[]{53.8167, 30.9667});
        CITY_COORDINATES.put("костюковичи", new double[]{53.3500, 32.0667});
        CITY_COORDINATES.put("климовичи", new double[]{53.6167, 31.9667});
        CITY_COORDINATES.put("чериков", new double[]{53.5667, 31.3833});
        CITY_COORDINATES.put("краснополье", new double[]{53.3500, 31.4333});
        CITY_COORDINATES.put("быхов", new double[]{53.5167, 30.2500});
        CITY_COORDINATES.put("кировск", new double[]{53.2833, 29.4667});
        CITY_COORDINATES.put("глуск", new double[]{53.4833, 28.5833});
        CITY_COORDINATES.put("кличев", new double[]{53.9667, 29.3167});
        CITY_COORDINATES.put("круглое", new double[]{54.2500, 29.8167});
        CITY_COORDINATES.put("белыничи", new double[]{54.0000, 30.0333});
    }

    public double[] getCoordinates(String cityName) {
        return CITY_COORDINATES.get(cityName.toLowerCase().trim());
    }

    /**
     * Рассчитывает расстояние между двумя городами с использованием
     * динамического коэффициента коррекции в зависимости от расстояния по прямой.
     *
     * Логика коэффициентов:
     * - Короткие маршруты (< 500 км): дороги сильно извилистые, коэффициент 1.3
     * - Средние маршруты (500-1500 км): коэффициент 1.25
     * - Длинные маршруты (1500-3000 км): больше прямых участков, коэффициент 1.2
     * - Очень длинные (> 3000 км): магистральные дороги, коэффициент 1.15
     */
    public double calculateDistance(String fromCity, String toCity) {
        double[] from = getCoordinates(fromCity);
        double[] to = getCoordinates(toCity);

        if (from == null || to == null) {
            return 1000; // Дефолтное расстояние, если город не найден
        }

        // Считаем расстояние по прямой (формула гаверсинусов)
        double directDistance = calculateHaversine(from, to);

        // Применяем динамический коэффициент в зависимости от расстояния
        double correctionFactor;
        if (directDistance < 500) {
            correctionFactor = 1.3; // Короткие маршруты: +30%
        } else if (directDistance < 1500) {
            correctionFactor = 1.25; // Средние: +25%
        } else if (directDistance < 3000) {
            correctionFactor = 1.2; // Длинные: +20%
        } else {
            correctionFactor = 1.15; // Очень длинные: +15%
        }

        return directDistance * correctionFactor;
    }

    /**
     * Формула гаверсинусов для расчета расстояния между двумя точками
     * на поверхности сферы (Земли) по их координатам.
     */
    private double calculateHaversine(double[] from, double[] to) {
        final int R = 6371; // Радиус Земли в км

        double latDistance = Math.toRadians(to[0] - from[0]);
        double lonDistance = Math.toRadians(to[1] - from[1]);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(from[0])) * Math.cos(Math.toRadians(to[0]))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}