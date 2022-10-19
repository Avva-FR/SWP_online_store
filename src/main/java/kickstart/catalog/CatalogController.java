package kickstart.catalog;

import lombok.SneakyThrows;

import org.javamoney.moneta.Money;

import static org.salespointframework.core.Currencies.EURO;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;

import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
class CatalogController {
	private static final Quantity NONE = Quantity.of(0);

	private final ShopItemCatalog shopItemCatalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final BusinessTime businessTime;
	private final ImagesRepo imagesRepo;

	CatalogController(ShopItemCatalog shopItemCatalog, UniqueInventory<UniqueInventoryItem> inventory,
					  BusinessTime businessTime, ImagesRepo imagesRepo) {

		this.shopItemCatalog = shopItemCatalog;
		this.inventory = inventory;
		this.businessTime = businessTime;
		this.imagesRepo = imagesRepo;
	}

	/**
	 * @return All ShopItems with ShopItemType BOOK
	 */
	@GetMapping(value = "/bookshop")
	public String bookCatalog(@RequestParam(value = "sort", defaultValue = "name_asc") String sort, Model model) {
		model.addAttribute("shopItemCatalog", shopItemCatalog.findByType(ShopItem.ShopItemType.BOOK));
		model.addAttribute("title", "shopItemCatalog.book.title");

		sort(sort, model);
		return "shop";
	}

	/**
	 * @return All ShopItems with ShopItemType DVD
	 */
	@GetMapping(value = "/dvdshop")
	public String dvdCatalog(@RequestParam(value = "sort", defaultValue = "name_asc") String sort, Model model) {
		model.addAttribute("shopItemCatalog", shopItemCatalog.findByType(ShopItem.ShopItemType.DVD));
		model.addAttribute("title", "shopItemCatalog.dvd.title");

		sort(sort, model);
		return "shop";
	}

	/**
	 * @return All ShopItems with ShopItemType CD
	 */
	@GetMapping(value = "/cdshop")
	public String cdCatalog(@RequestParam(value = "sort", defaultValue = "name_asc") String sort, Model model) {
		model.addAttribute("shopItemCatalog", shopItemCatalog.findByType(ShopItem.ShopItemType.CD));
		model.addAttribute("title", "shopItemCatalog.cd.title");

		sort(sort, model);
		return "shop";
	}

	@GetMapping("shopItem/{shopItem}")
	String editShopItem(@PathVariable ShopItem shopItem, Model model) {

		var quantity = inventory.findByProductIdentifier(shopItem.getId())
				.map(InventoryItem::getQuantity)
				.orElse(NONE);

		model.addAttribute("shopitem", shopItem);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE));

		return "productdetail";
	}

	@PostMapping("/shopItemTitle")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateTitle(@RequestParam("pid") ShopItem shopItem, @RequestParam("newName") String newName) {
		shopItem.setName(newName);
		shopItemCatalog.save(shopItem);
			return "redirect:/shopItem/" + shopItem.getId();
	}

	@PostMapping("/shopItemPrice")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updatePrice(@RequestParam("pid") ShopItem shopItem, @RequestParam("newPrice") float newPrice) {
		shopItem.setPrice(Money.of(newPrice, EURO));
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}

	@PostMapping("/shopItemCreator")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateCreator(@RequestParam("pid") ShopItem shopItem, @RequestParam("newCreator") String newCreator) {
		shopItem.setCreator(newCreator);
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}
	//currently unused
	@PostMapping("/shopItemPublisher")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updatePublisher(@RequestParam("pid") ShopItem shopItem,
								  @RequestParam("newPublisher") String newPublisher) {
		shopItem.setPublisher(newPublisher);
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}

	//currently unused
	@PostMapping("/shopItemIdNumber")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateIdNumber(@RequestParam("pid") ShopItem shopItem,
								 @RequestParam("newIdNumber") String newIdNumber) {
		shopItem.setIdnumber(newIdNumber);
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}

	@PostMapping("/shopItemDescription")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateDescription(@RequestParam("pid") ShopItem shopItem,
									@RequestParam("newDescription") String newDescription) {
		shopItem.setDescription(newDescription);
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}

	@PostMapping("/shopItemGenre")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public String updateGenre(@RequestParam("pid") ShopItem shopItem, @RequestParam("newGenre") String newGenre) {
		shopItem.setGenre(newGenre);
		shopItemCatalog.save(shopItem);

		return "redirect:/shopItem/" + shopItem.getId();
	}

	/**
	 * @param itemGenre Genre of the ShopItem
	 * @return All ShopItems with the same genre
	 */
	@GetMapping("/genres/{itemGenre}")
	String bookGenres(@RequestParam(value = "sort",defaultValue = "name_asc") String sort,
					  @PathVariable String itemGenre, Model model) {

		List<ShopItem> items = shopItemCatalog.findAll().stream()
				.filter(shopItem -> shopItem.getGenre().equalsIgnoreCase(itemGenre))
				.flatMap(item -> shopItemCatalog.findByName(item.getName()).stream())
				.collect(Collectors.toList());

		model.addAttribute("shopItemCatalog", items);
		model.addAttribute("title", "shopItemCatalog.item.title");

		sort(sort, model);
		return "shop";
	}

	/**
	 * @param sort Defines the direction and attribute to sort by
	 * @param keyword  Substring for search value
	 * @return All ShopItems that contain keyword as a substring
	 */
	@GetMapping("/search")
	String searchShopItemCatalog(@RequestParam(value = "sort",defaultValue = "name_asc") String sort,
								 @RequestParam("keyword") String keyword, Model model) {
		List<ShopItem> shopItemList = shopItemCatalog.findAll()
				.stream()
				.filter(shopItem -> shopItem.getName().toLowerCase().contains(keyword.toLowerCase()))
				.flatMap(item -> shopItemCatalog.findByName(item.getName()).stream())
				.collect(Collectors.toList());

		model.addAttribute("shopItemCatalog", shopItemList);
		model.addAttribute("title", "shopItemCatalog.item.title");

		sort(sort, model);
		return "shop";
	}

	/**
	 * @param sort Value defines attribute and direction to sort by
	 * @param model Model to compare and sort
	 */
	private static void sort(String sort, Model model) {
		switch (sort) {
			case "price_asc":
				model.addAttribute("sort", Comparator.comparing(ShopItem::getPrice));
				break;
			case "price_desc":
				model.addAttribute("sort", Comparator.comparing(ShopItem::getPrice).reversed());
				break;
			case "name_asc":
				model.addAttribute("sort", Comparator.comparing(ShopItem::getName));
				break;
			case "name_desc":
				model.addAttribute("sort", Comparator.comparing(ShopItem::getName).reversed());
				break;
			default:
				model.addAttribute("sort", Comparator.comparing(ShopItem::getName));
				break;
		}
	}


	@GetMapping("/additem")
	@PreAuthorize("hasRole('BOSS')")
	public String addItemForm(AddItemForm form, Model model) {
		return "additem";
	}

	@SneakyThrows
	@PostMapping("/additem")
	@PreAuthorize("hasRole('BOSS')")
	public String addItem(AddItemForm form, @RequestPart("image") MultipartFile multipartFile, Errors result,
						  Model model, MultipartProperties properties) {
		if (result.hasErrors()){
			return "additem";
		}

		// Check if there is already a user with this name present
		if (shopItemCatalog.existsByProductIdentifier_Id(form.getIdnumber())) {
			result.rejectValue("idnumber", "AddItemForm.IdTaken");
			return "additem";
		}

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

		ProductImage dbImage = ProductImage.builder()
				.name(UUID.randomUUID().toString())
				.content(multipartFile.getBytes())
				.build();

		ProductImage storedImage = imagesRepo.save(dbImage);

		ShopItem entity = form.toShopItem(fileName);
		ShopItem item = shopItemCatalog.save(entity);
		inventory.save(new UniqueInventoryItem(item, Quantity.of(form.getQuantity())));

		String uploadDir = "user-photos/";
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		return "redirect:/inventory";
	}


}