package com.online.languages.study.studymaster.data;


import android.content.Context;

import java.util.ArrayList;

public class ImageMapsData {


    ArrayList<ImageData> data = new ArrayList<>();

    public ImageMapsData(Context _context) {
        prepareData();
    }


    public ImageData getMapInfoById(String id) {

        ImageData imageData = new ImageData();

        for (ImageData item: data) {
            if (item.id.equals(id)) {
                imageData = item;
            }
        }

        return imageData;
    }

    private void prepareData() {

        data.add(new ImageData("Славянские племена",
                "https://commons.wikimedia.org/wiki/File:Slav-7-8-obrez.png",
                "10010510",
                "maps/Slav2.png",
                "Карта расселения славян и их соседей на конец VIII века. Границы некоторых государств показаны начиная с VII в. Основана в восточнославянской части в основном на археологических картах и описаниях, в частности взятых из работ В.В.Седова (в основном данные по славянам) и тома «Финно-угры и балты в эпоху средневековья» (1987) из серии Археология СССР (в основном данные по балтам и финно-уграм)."));

        data.add(new ImageData(
                "Русь в XI в.", "https://commons.wikimedia.org/wiki/File:Rus-1015-1113.png",
                "10010520",
                "maps/rus_11v.png",
                "Киевская Русь в 1015-1113 гг."));


        data.add(new ImageData(
                "Русь в XII в.",
                "https://commons.wikimedia.org/wiki/File:Principalities_of_Kievan_Rus%27_(1054-1132)_ru2.svg",
                "10010530",
                "maps/rus_1132.png",
                "Княжества поздней Киевской Руси (после смерти Ярослава I в 1054)."));

        data.add(new ImageData(
                "Середина XIII в.",
                "https://commons.wikimedia.org/wiki/File:Kievan_Rus_in_1237_(ru).svg",
                "10020510",
                "maps/rus_1237.png",
                "Карта Киевской Руси в 1237 г."));


        data.add(new ImageData(
                "Борьба Руси в XIII в.",
                "https://history.wikireading.ru/23144",
                "10020515", "maps/map_rus_13_defense.jpg",
                "Борьба Руси против иноземных захватчиков в первой половине XIII в."));


        data.add(new ImageData(
                "1239-1245 гг.",
                "https://ru.wikipedia.org/wiki/%D0%A4%D0%B0%D0%B9%D0%BB:Rus-1240-nevski.png",
                "10020520", "maps/rus_1240_nevski.png",
                "Отражение немецкой и шведской агрессии Александром Невским (1239-1245)."));


        data.add(new ImageData(
                "XIV-XV вв.", "https://commons.wikimedia.org/wiki/File:Muscovy_1300-1462.png",
                "10020530", "maps/rus_1462.png",
                "Расширение Московского княжества в 1300-1462 годах."));

        data.add(new ImageData(
                "Куликовская битва",
                "https://encyclopedia.mil.ru/encyclopedia/history/more.htm?id=12055282@cmsArticle",
                "10020540", "maps/rus_battle_1380.jpg",
                "Схема Куликовской битвы 8 сентября 1380 г."
        ));

        data.add(new ImageData(
                "Конец XIV в.", "https://commons.wikimedia.org/wiki/File:Rus-1389.png",
                "10020550", "maps/rus_1389.png",
                "Русские земли в 1389 году."));

        data.add(new ImageData("XVI в.",
                "https://ru.wikipedia.org/wiki/%D0%A4%D0%B0%D0%B9%D0%BB:%D0%9A%D0%B0%D1%80%D1%82%D0%B0-%D0%BC%D0%BE%D1%81%D0%BA_%D0%BA%D0%BD%D1%8F%D0%B6%D0%B5%D1%81%D1%82%D0%B2%D0%B0.jpg",
                "10030510", "maps/rus_1520.jpg",
                "Приблизительная карта распространения власти Московского княжества с 1300 по 1521 год"));


        data.add(new ImageData("2-я пол. XVI в.",
                "https://commons.wikimedia.org/wiki/File:Livon-war-post_1583.png",
                "10030520", "maps/rus_1583.png",
                "Территориальные изменения в результате Ливонской войны."));

        data.add(new ImageData("XVI-XVIII в.",
                "https://commons.wikimedia.org/wiki/File:Growth_of_Russia_1547-1725_true_borders.png",
                "10030530", "maps/rus_1725.png",
                "Рост территории России с 1547 по 1725 года."));


        data.add(new ImageData("Россия в 1613-1914 гг.",
                "https://commons.wikimedia.org/wiki/File:Growth_of_Russia_1613-1914.png",
                "10030540", "maps/russia_1613-1914.png",
                "Расширение территории России с 1613 по 1914 гг."));

        data.add(new ImageData("1670-1671 гг.",
                "http://rushist.com/index.php/tutorials/plat-tutorial/128-plat-tut-84",
                "10030550", "maps/map_razin.jpg",
                "Крестьянская война под предводительством Степана Разина в 1670–1671 гг."));



        data.add(new ImageData("1708 г.",
                "https://commons.wikimedia.org/wiki/File:Subdivisions_of_Russia_in_1708_(ru).svg",
                "10040510", "maps/rus_1708.png",
                "Карта, показывающая административно-территориальные единицы Царства русского в 1708 году."));


        data.add(new ImageData("1700 – 1721 гг.",
                "https://history.wikireading.ru/23688",
                "10040520", "maps/map_war_north.jpg",
                "Северная война. 1700 – 1721 гг."));


        data.add(new ImageData("1709 г.",
                "https://fox-calculator.ru/petr-1/konturnyie-kartyi-poltavskoe-srazhenie-1709-goda/",
                "10040530", "maps/map_battle_1709.jpg",
                "Карта передвижения русских и шведских войск. Полтавское сражение 27 июня 1709 года."));

        data.add(new ImageData("1756—1763 гг.",
                "https://fishki.net/2626595-semiletnjaja-vojna-1756-1763.html",
                "10040540", "maps/map_war_1756.jpg",
                "Россия в Семилетней войне 1756-1763 гг."));

        data.add(new ImageData("1773—1775 гг.",
                "http://rushist.com/index.php/historical-notes/2048-vosstanie-pugachjova-kratko",
                "10040550", "maps/map_pugachev.jpg",
                "Крестьянская война 1773—1775 годов под предводительством Емельяна Пугачёва."));


        data.add(new ImageData("1812 гг.",
                "https://history.wikireading.ru/23688",
                "10050510", "maps/map_war_1812.jpg",
                "Нашествие армии Наполеона на Россию. 1812 г."));


        data.add(new ImageData("Бородинское сражение",
                "https://army-blog.ru/borodinskoe-srazhenie/",
                "10050520", "maps/map_borodino.jpg",
                "Карта-схема сражения у деревни Бородино 26 августа (7 сентября) 1812 года."));

        data.add(new ImageData("1853-1856 гг.",
                "https://history.wikireading.ru/23688",
                "10050550", "maps/map_war_1856.jpg",
                "Крымская война 1853-1856 гг. Карта театра военных действий воюющих сторон на суше и на море в бассейне Чёрного моря."));

        data.add(new ImageData("1877–1878 гг.",
                "https://history.wikireading.ru/23688",
                "10050560", "maps/map_war_1878.jpg",
                "Русско-турецкая война 1877 – 1878 гг."));


        data.add(new ImageData(" 1878 г.",
                "https://ru.wikipedia.org/wiki/%D0%9A%D1%80%D1%8B%D0%BC%D1%81%D0%BA%D0%B0%D1%8F_%D0%B2%D0%BE%D0%B9%D0%BD%D0%B0",
                "10050580", "maps/map_stephan_borders.jpg",
                "Границы балканских государств и России по Сан-Стефанскому мирному договору."));


        data.add(new ImageData("Конец XIX в.",
                "http://starye-karty.litera-ru.ru/karty/rossiiskaya-imperia-1890-na-stenu-kabinet.html",
                "10060510", "maps/map_empire_1890.jpg",
                "Карта Российской империи конца XIX века. Карта является дизайнерской доработкой карты конца 19 века, по точной датировке - около 1890 года. "));

        data.add(new ImageData("1904–1905 гг.",
                "https://history.wikireading.ru/23797",
                "10060520", "maps/map_war_1905.jpg",
                "Русско-японская война 1904–1905 гг."));

        data.add(new ImageData("1904 г.",
                "https://history.wikireading.ru/23797",
                "10060530", "maps/map_battle_artur.jpg",
                "Оборона Порт-Артура. События в "));

        data.add(new ImageData("1918-1920 г.",
                "https://www.rusempire.ru/fotografii/maps/karti-voennich-deystviy/karty-voennykh-dejstvij-3640.html",
                "10070520", "maps/map_war_civil.jpg",
                "Гражданская война и военная интервенция в России. Развертывание военной интервенции Антанты в Гражданской войне."));


        data.add(new ImageData("1941-1942 г.",
                "https://history.wikireading.ru/23797",
                "10070540", "maps/map_war_1941.jpg",
                "ВЕЛИКАЯ ОТЕЧЕСТВЕННАЯ ВОЙНА (июнь 1941-ноябрь 1942 года)"));

        data.add(new ImageData("Сталинградская битва",
                "https://history.wikireading.ru/23797",
                "10070550", "maps/map_war_stalingrad.jpg",
                "СТАЛИНГРАДСКАЯ БИТВА. КОНТРНАСТУПЛЕНИЕ СОВЕТСКИХ ВОЙСК (19 ноября – 31 декабря 1942 г.)"));

        data.add(new ImageData("1943 г.",
                "https://history.wikireading.ru/23797",
                "10070560", "maps/map_battle_kurk.jpg",
                "КУРСКАЯ БИТВА (5 июля – 23 августа 1943 г.)"));

        data.add(new ImageData("1942–1945 г.",
                "https://history.wikireading.ru/23797",
                "10070570", "maps/map_war_1945.jpg",
                "ВЕЛИКАЯ ОТЕЧЕСТВЕННАЯ ВОЙНА (ноябрь 1942 – май 1945 г.)"));


        data.add(new ImageData("СССР",
                "http://www.maps-world.ru/usssr.htm",
                "10070590", "maps/map_ussr.jpg",
                "Карта СССР на русском языке. CCCP - самое большое государство в мире с 1922 по 1991 год. Союз Советских Социалистических Республик по площади был самой крупной страной в мире и занимал шестую часть всей поверхности суши. СССР состоял из 15 республик и имел площадь 22,4 миллиона квадратных километров."));


        data.add(new ImageData("Карта РФ, 2000 г.",
                "http://maps-of-world.ru/map-russia.htm",
                "10080510", "maps/map_russia.jpg",
                "Административная карта России. 2000 г."));

    }
}
