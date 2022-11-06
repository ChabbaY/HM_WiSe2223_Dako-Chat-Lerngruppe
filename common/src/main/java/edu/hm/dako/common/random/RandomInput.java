package edu.hm.dako.common.random;

import java.util.HashMap;
import java.util.Random;

/**
 * zufällige Werte fürs Testen generieren
 *
 * @author Linus Englert
 */
public class RandomInput {
    /**
     * hash map to store already produced random names, they won't be produced a second time
     */
    private static final HashMap<Integer, String> randomNames = new HashMap<>();
    /**
     * Konstruktor
     */
    public RandomInput() {
    }

    /**
     * zufälliger User Name
     *
     * @return random name
     */
    public static String randomName() {
        Random random = new Random();
        Name[] namen = Name.values();
        String randomName = namen[random.nextInt(namen.length)].toString() + "_" + (random.nextInt(8999) + 1000);
        while (randomNames.containsValue(randomName)) {
            randomName = namen[random.nextInt(namen.length)].toString() + "_" + (random.nextInt(8999) + 1000);
        }
        randomNames.put(randomName.hashCode(), randomName);
        return randomName;
    }

    /**
     * beliebige Namen für das Bilden von User Namen
     */
    private enum Name {
        //A
        abba, adele, admin, afraid_to_feel, agustin, aha, alex, alexander, american_idiot, amy, amy_winehouse,
        anastasia, anastasiia, anna, anne, another_brick_in_the_wall, augustin, axel,
        //B
        back_to_black, bad, bart, beat_it, ben, benedikt, bernd, berta, bianka, billie_jean, bohemian_rhapsody,
        bon_jovi, boris, boulevard_of_broken_dreams, bruno, bruno_mars,
        //C
        calvin, carina, castle_of_glass, celina, charlotte, check24, chicago, chris, christian, christian_lindner,
        christine, christoph, ciri, clara, coldplay, crazy_little_thing_called_love,
        //D
        daniel, daniela, david, david_guetta, dead_by_daylight, denise, dennis, devil, donald, dont_stop_me_now, dora,
        doris, dragonborn,
        //E
        elias, elisabeth, ella, elton_john, emblem, emil, emilia, eminem, emma, ena, ester, euphoria,
        //F
        fading_like_a_flower, feel, felix, finn, fiona, flo, florian, franz, franziska, franziskus, fynn,
        //G
        gabriel, gabriela, gabriele, geralt_von_riva, giovanna, giovanni, green_day, guns_n_roses, gustav,
        //H
        habits, hanna, hannah, hannes, hans, hans_zimmer, harald, harry, he_who_brings_the_night, heart_of_courage,
        heinrich, henri, henry, hermine, horst, hunter,
        //I
        ida, imagine_dragons, im_still_standing, ingrid, in_the_army_now, isabel, isabella, ismar, irmi, its_my_life,
        //J
        jan, jana, janina, janine, jannik, jasmin, jasmina, johann, johannes, jonas, julia, julian, juliana,
        //K
        karen, karin, karl, karla, katha, katharina, katja, kilian, killer_queen, klara, korbinian, kristijan,
        //L
        lambada, laurenz, lea, leah, lennard, lennart, lenny, lf_system, lina, linkin_park, linnea, linus, liz, lizzy,
        leo, leonard, leonardo, loreen, lorenz, losing_my_religion, lost_on_you, louis, louisa, louise,
        love_is_a_losing_game, lp, ludwig, luis, luisa, luise,
        //M
        madcon, manuel, manuela, marcel, maria, marianne, mario, marion, markus, marti, martin, massimo, matteo,
        mattheo, max, maxime, maxine, melanie, mia, michael, michaela, mila, milow, moritz,
        //N
        nah_neh_nah, natalia, natasha, nele, nessie, noah, nora, november_rain, numb,
        //O
        oh_my_god, olaf, olaf_scholz, oskar, otto,
        //P
        paint_it_black, party_like_a_russian, pascal, patricia, paul, paula, paulina, pauline, peter, petra, philip,
        philipp, pink, pink_floyd, portal,
        //Q
        queen, quentin, quirin,
        //R
        raphael, raphaela, richard, ron, ronald, rosalinde, rosamunde,
        //S
        sabine, samuel, sariel, satisfactory, sebastian, selina, sid_meier, siuuuuu, simon, simona, simone, skyrim,
        sofia, sofie, some_unholy_war, sophia, sophie, sonja, stefan, stefanie, stephan, stephanie, susanne, susie,
        sweet_dreams,
        //T
        theodor, they_dont_care_about_us, the_show_must_go_on, this_i_love, thriller, tim, timo, triss_merigold,
        tri_poloski, two_steps_from_hell,
        //U
        ulrich, ulrike, urmela, ursula,
        //V
        v, valentin, valentina, valerian, vanessa, verena, victory, viktor,
        //W
        wilhelm, wilhelmina,
        //X
        x, xanthippe, xenophon,
        //Y
        yennefer_von_vengerberg, you_give_love_a_bad_name, yvonne,
        //Z
        zacharias, zen
    }
}