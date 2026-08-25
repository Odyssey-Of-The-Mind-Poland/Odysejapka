<main class="min-h-screen bg-muted/30 py-12 px-4">
	<div class="mx-auto max-w-3xl">
		<div class="mb-8">
			<h1 class="text-3xl font-bold tracking-tight">Polityka prywatności</h1>
			<p class="mt-2 text-muted-foreground">Fundacja Odyssey of the Mind Polska (Odyseja Umysłu)</p>
		</div>

		<div class="flex flex-col gap-6">

			<!-- Administrator -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-3">Administrator danych osobowych</h2>
				<p class="text-sm text-muted-foreground leading-relaxed">
					Fundacja Odyssey of the Mind Polska, z siedzibą w Gdańsku (80-283) przy ul. Zacnej 10,
					jest administratorem danych osobowych przetwarzanych przez serwis odyseja.org.
					Kontakt dostępny pod adresem: <a href="mailto:odyseja@odyseja.org" class="text-primary underline-offset-4 hover:underline">odyseja@odyseja.org</a>.
				</p>
			</div>

			<!-- Cele przetwarzania -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-4">Cele przetwarzania danych</h2>
				<div class="flex flex-col gap-3">
					{#each [
						{ title: 'Uczestnictwo w programie', desc: 'Na podstawie zgody, do momentu jej odwołania lub upływu okresu przedawnienia roszczeń.' },
						{ title: 'Wizerunek uczestników', desc: 'Fotografie i nagrania z wydarzeń do promocji programu via internet i media społecznościowe, na podstawie zgody wyrażonej przez stawiennictwo.' },
						{ title: 'Odpowiedzi na zapytania', desc: 'Przechowywanie korespondencji elektronicznej, telefonicznej i formularzowej na podstawie uzasadnionego interesu fundacji.' },
						{ title: 'Badania efektywności', desc: 'Analiza skuteczności działań fundacji i identyfikacja obszarów do poprawy na podstawie uzasadnionego interesu.' },
						{ title: 'Realizacja zamówień', desc: 'Na podstawie umowy oraz uzasadnionego interesu w zakresie dokumentacji i rozliczalności.' },
						{ title: 'Pozyskiwanie darowizn', desc: 'Na podstawie obowiązku prawnego dotyczącego rachunkowości i podatków.' },
						{ title: 'Analiza strony i kampanii', desc: 'Badanie skuteczności serwisu i kampanii reklamowych na podstawie zgody na pliki cookies.' },
						{ title: 'Postępowania prawne', desc: 'Dochodzenie roszczeń i obrona przed nimi na podstawie uzasadnionego interesu fundacji.' },
						{ title: 'Archiwizowanie dokumentów', desc: 'Przechowywanie dokumentów zgodnie z obowiązkami prawnymi i wymaganiami archiwizacyjnymi.' },
					] as item, i (i)}
						<div class="flex gap-3">
							<div class="flex h-6 w-6 shrink-0 items-center justify-center rounded-md bg-primary/10 text-xs font-semibold text-primary mt-0.5">
								{i + 1}
							</div>
							<div>
								<p class="text-sm font-medium">{item.title}</p>
								<p class="text-sm text-muted-foreground mt-0.5">{item.desc}</p>
							</div>
						</div>
					{/each}
				</div>
			</div>

			<!-- Odbiorcy danych -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-3">Odbiorcy danych</h2>
				<p class="text-sm text-muted-foreground leading-relaxed mb-3">
					Dane mogą być przekazywane podmiotom świadczącym usługi na rzecz fundacji:
				</p>
				<ul class="flex flex-col gap-1.5">
					{#each [
						'Dostawcy usług pocztowych i kurierskich',
						'Kancelarie prawne i biura rachunkowe',
						'Operatorzy płatności i banki',
						'Dostawcy hostingu i usług IT',
						'Organy państwowe (na podstawie przepisów prawa)',
						'Patroni medialni i serwisy społecznościowe: Facebook Ireland Ltd., LinkedIn Ireland Unlimited Company, YouTube (Google LLC), Vimeo LLC',
					] as item}
						<li class="flex items-start gap-2 text-sm text-muted-foreground">
							<span class="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary/60"></span>
							{item}
						</li>
					{/each}
				</ul>
			</div>

			<!-- Przekazanie poza UE -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-3">Przekazanie danych poza UE</h2>
				<p class="text-sm text-muted-foreground leading-relaxed">
					Dane mogą być przetwarzane przez podmioty działające poza Europejskim Obszarem Gospodarczym,
					w tym: Google LLC, LinkedIn Ireland Unlimited Company, Vimeo LLC oraz Facebook Ireland Ltd.
					w celach analityki, reklamy i publikacji treści. Dane zawarte w plikach cookies mają charakter anonimowy.
				</p>
			</div>

			<!-- Prawa użytkownika -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-4">Prawa użytkownika</h2>
				<div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
					{#each [
						'Dostęp do danych osobowych',
						'Sprostowanie i usunięcie danych',
						'Ograniczenie przetwarzania',
						'Przenoszenie danych',
						'Wniesienie sprzeciwu wobec przetwarzania',
						'Cofnięcie zgody w dowolnym momencie',
					] as right}
						<div class="flex items-center gap-2 rounded-lg border bg-muted/40 px-3 py-2 text-sm">
							<span class="h-1.5 w-1.5 shrink-0 rounded-full bg-primary"></span>
							{right}
						</div>
					{/each}
				</div>
				<p class="text-sm text-muted-foreground">
					W przypadku naruszenia przepisów RODO przysługuje prawo wniesienia skargi do
					<span class="font-medium text-foreground">Prezesa Urzędu Ochrony Danych Osobowych (PUODO)</span>.
				</p>
			</div>

			<!-- Dobrowolność i automatyzacja -->
			<div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
				<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
					<h2 class="text-lg font-semibold tracking-tight mb-2">Dobrowolność podania danych</h2>
					<p class="text-sm text-muted-foreground leading-relaxed">
						Podanie danych osobowych jest dobrowolne. Jest ono wymagane wyłącznie w przypadku
						uczestnictwa w programie lub złożenia zamówienia.
					</p>
				</div>
				<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
					<h2 class="text-lg font-semibold tracking-tight mb-2">Decyzje zautomatyzowane</h2>
					<p class="text-sm text-muted-foreground leading-relaxed">
						Dane osobowe nie są wykorzystywane do podejmowania zautomatyzowanych decyzji
						ani do profilowania użytkowników.
					</p>
				</div>
			</div>

			<!-- Polityka cookies -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-4">Polityka plików cookies</h2>

				<div class="flex flex-col gap-4">
					<div>
						<h3 class="text-sm font-semibold mb-1.5">Automatycznie zbierane dane</h3>
						<p class="text-sm text-muted-foreground">
							Serwis automatycznie gromadzi adres IP, typ przeglądarki, system operacyjny
							oraz aktywności, jednak bez identyfikacji tożsamości użytkownika.
						</p>
					</div>

					<div class="h-px bg-border"></div>

					<div>
						<h3 class="text-sm font-semibold mb-3">Typy plików cookies</h3>
						<div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
							<div class="rounded-lg border bg-muted/40 p-3">
								<p class="text-sm font-medium">Sesyjne</p>
								<p class="text-xs text-muted-foreground mt-0.5">Usuwane po wylogowaniu lub zamknięciu przeglądarki.</p>
							</div>
							<div class="rounded-lg border bg-muted/40 p-3">
								<p class="text-sm font-medium">Trwałe</p>
								<p class="text-xs text-muted-foreground mt-0.5">Przechowywane przez z góry określony czas.</p>
							</div>
						</div>
					</div>

					<div class="h-px bg-border"></div>

					<div>
						<h3 class="text-sm font-semibold mb-1.5">Zastosowanie cookies</h3>
						<ul class="flex flex-col gap-1.5">
							{#each [
								'Prezentacja treści z YouTube (Google LLC) i Vimeo (Vimeo LLC)',
								'Anonimowa statystyka strony przez Google Analytics',
								'Funkcje społecznościowe Facebook (Facebook Ireland Ltd.)',
							] as use}
								<li class="flex items-start gap-2 text-sm text-muted-foreground">
									<span class="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary/60"></span>
									{use}
								</li>
							{/each}
						</ul>
					</div>
				</div>
			</div>

			<!-- Bezpieczeństwo i odpowiedzialność -->
			<div class="rounded-xl border bg-card text-card-foreground shadow-sm p-6">
				<h2 class="text-lg font-semibold tracking-tight mb-3">Bezpieczeństwo i odpowiedzialność</h2>
				<p class="text-sm text-muted-foreground leading-relaxed mb-2">
					Fundacja stosuje szyfrowane połączenie SSL przy przesyłaniu danych osobowych
					i logowaniu do systemu Odyseja Online.
				</p>
				<p class="text-sm text-muted-foreground leading-relaxed">
					Niniejsza polityka dotyczy wyłącznie serwisu odyseja.org. Fundacja nie ponosi
					odpowiedzialności za zasady prywatności obowiązujące na stronach zewnętrznych.
				</p>
			</div>

			<!-- Zmiany polityki -->
			<div class="rounded-xl border border-dashed bg-muted/20 p-6">
				<p class="text-sm text-muted-foreground text-center">
					Fundacja zastrzega sobie prawo do zmiany niniejszej polityki poprzez
					publikację nowej wersji na stronie odyseja.org.
				</p>
			</div>

		</div>
	</div>
</main>
