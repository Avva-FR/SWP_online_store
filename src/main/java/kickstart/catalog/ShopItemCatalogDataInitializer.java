package kickstart.catalog;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

import static org.salespointframework.core.Currencies.EURO;

@Component
@Order(20)
class ShopItemCatalogDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(ShopItemCatalogDataInitializer.class);

	private final ShopItemCatalog shopItemCatalog;

	ShopItemCatalogDataInitializer(ShopItemCatalog shopItemCatalog) {
		Objects.requireNonNull(shopItemCatalog, "ShopItemCatalog must not be null");

		this.shopItemCatalog = shopItemCatalog;
	}

	@Override
	public void initialize() {

		if (shopItemCatalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");
		shopItemCatalog.save(new ShopItem("Matrix", Money.of(420, EURO), "Keanu Reeves", UUID.randomUUID().toString(),
				"Keanu Reeves", "Keanu Reeves spielt in diesem Film, der NICHT Teil einer Trilogie ist...",
				"DVD_matrix.png", "Action", ShopItem.ShopItemType.DVD));
		shopItemCatalog.save(new ShopItem("Hypa Hypa", Money.of(6, EURO), "Eskimo Callboy",
				UUID.randomUUID().toString(), "Inuit Music", "Das beste Crossover der modernen Zeit",
				"CD_hypa.png", "Single", ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Harry Potter und das nervige Java Framework", Money.of(9.99, EURO),
				"J. K. Rowling (nicht Frank Herbert)", UUID.randomUUID().toString(), "Carlsen",
				"Harry Potter und das nervige Java Framework, aber definitiv nicht Dune!", "book_dune.png", "Fiction",
				ShopItem.ShopItemType.BOOK));

		shopItemCatalog.save(new ShopItem("Benny The Bird", Money.of(3, EURO), "I Drink My Coffee Alone",
				UUID.randomUUID().toString(), "IDMCA", "Ein Vogel und seine Reise zu McDonalds",
				"CD_bird.png", "Single", ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Ye allright this is a CD", Money.of(420, EURO), "CD-Creator2",
				UUID.randomUUID().toString(), "This is rubbish", "Yes it works, but not well", "book_dune.png", "Horror",
				ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("New Kids Turbo", Money.of(5, EURO), "Holland",
				UUID.randomUUID().toString(), "Feuerball Junge", "Blood Sweat Hardcore! Ein Klassiker für alt und jung",
				"DVD_kids.png", "Comedy", ShopItem.ShopItemType.DVD));
		shopItemCatalog.save(new ShopItem("Im toten Winkel", Money.of(999, EURO), "Michael Roitzsch",
				UUID.randomUUID().toString(), "Blue Beam", "Die Filmszene ist bis in die TU Dresden vorgedrungen!",
				"DVD_winkel.png", "Action", ShopItem.ShopItemType.DVD));
		shopItemCatalog.save(new ShopItem("Per Anhalter durch die Galaxis", Money.of(42.00, EURO), "Douglas Adams",
				UUID.randomUUID().toString(), "Kein & Aber", "DON'T PANIC!", "book_42.png", "Documentary",
				ShopItem.ShopItemType.BOOK));
		shopItemCatalog.save(new ShopItem("Die Bibel", Money.of(6.66, EURO), "Gott™", UUID.randomUUID().toString(),
				"Christentum",
				"Die definitiv wahren und zeitgemäßen Abenteuer Jesu und definitiv wahre und zeitgemäße andere Geschichten.",
				"book_bible.png", "Fiction", ShopItem.ShopItemType.BOOK));
		shopItemCatalog.save(new ShopItem("Fortran for Humans", Money.of(69.69, EURO), "Rich Didday, Rex Page",
				UUID.randomUUID().toString(), "West Publishing Co",
				"Endlich kann man der Lochscheibe gute Nacht sagen, Fortran saves the day", "book_fortran.png",
				"Horror", ShopItem.ShopItemType.BOOK));
		shopItemCatalog.save(new ShopItem("How to be Alman", Money.of(7.99, EURO), "Unbekannt",
				UUID.randomUUID().toString(), "Riva",
				"How to be Alman: Liege reservieren, richtig gut flirten, Zettel im Hausflur – " +
						"und noch mehr deutsche Moves, die du draufhaben musst",
				"book_alman.png", "Humor", ShopItem.ShopItemType.BOOK));
		shopItemCatalog.save(new ShopItem("Die nicht geghostwritete Autobiografie des Andreas Domanowski",
				Money.of(420.00, EURO), "Gruppe 40", UUID.randomUUID().toString(), "TU Dresden",
				"Es ist sehr gut", "book_dune.png", "Documentary", ShopItem.ShopItemType.BOOK));
		shopItemCatalog.save(new ShopItem("Toxicity", Money.of(13.12, EURO), "System Of A Down",
				UUID.randomUUID().toString(), "publisher",
				"THEY'RE TRYING TO BUILD A PRISON", "CD_toxicity.png", "Meddl", ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Through the Fire and the Flames", Money.of(666, EURO), "DragonForce",
				UUID.randomUUID().toString(), "Inhuman Rampage", "This is here for the memes", "CD_ttfatf.png",
				"Meddl", ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Let Go", Money.of(12.30, EURO), "Avril Lavigne",
				UUID.randomUUID().toString(), "Arista Records",
				"Avrils erstes Studioalbum mit Rock-Krachern wie Sk8er Boi", "CD_letgo.png", "Poprock",
				ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Under My Skin", Money.of(15.00, EURO), "Avril Lavigne",
				UUID.randomUUID().toString(), "Arista Records", "Beschtes Album der Poprock-Ikone aus Kanada",
				"CD_undermyskin.png", "Poprock", ShopItem.ShopItemType.CD));
		shopItemCatalog.save(new ShopItem("Pippi Langstrumpf geht an Bord", Money.of(17.17, EURO), "Astrid Lindgren",
				UUID.randomUUID().toString(), "Oetinger", "Sie geht sofort dort an Bord, ohne ein Wort",
				"book_pippi.png", "Lehrbuch", ShopItem.ShopItemType.BOOK));
	}
}