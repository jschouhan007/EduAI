// FILE: app/src/main/java/com/eduquiz/pro/data/local/AppDatabase.kt
package com.eduquiz.pro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eduquiz.pro.data.model.Course
import com.eduquiz.pro.data.model.Difficulty
import com.eduquiz.pro.data.model.Question
import com.eduquiz.pro.data.model.QuizResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Database(entities = [Course::class, Question::class, QuizResult::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: run {
                val database = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "eduquiz.db")
                    .addCallback(SeedDatabaseCallback { INSTANCE })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = database
                database
            }
        }
    }
}

class RoomConverters {
    @TypeConverter fun fromDifficulty(value: Difficulty): String = value.name
    @TypeConverter fun toDifficulty(value: String): Difficulty = Difficulty.valueOf(value)
}

private class SeedDatabaseCallback(
    private val databaseProvider: () -> AppDatabase?
) : RoomDatabase.Callback() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        scope.launch {
            var database = databaseProvider()
            repeat(10) {
                if (database != null) return@repeat
                delay(100)
                database = databaseProvider()
            }
            database ?: return@launch
            if (database.courseDao().count() == 0) {
                val courses = listOf(
                    Course(id = 1, title = "General Science", description = "Build strong fundamentals in everyday science.", icon = "🔬"),
                    Course(id = 2, title = "Mathematics", description = "Practice arithmetic, algebra, and geometry basics.", icon = "📐"),
                    Course(id = 3, title = "World History", description = "Explore key events and civilizations.", icon = "🏛️"),
                    Course(id = 4, title = "English Grammar", description = "Improve usage, tense, and sentence structure.", icon = "📘"),
                    Course(id = 5, title = "Computer Science Basics", description = "Learn core computing concepts and logic.", icon = "💻")
                )
                database.courseDao().insertAll(courses)
            }
            if (database.questionDao().count() == 0) {
                val questions = mutableListOf<Question>()
                fun q(courseId: Int, difficulty: Difficulty, q: String, a: String, b: String, c: String, d: String, correct: String, explanation: String) {
                    questions.add(Question(courseId = courseId, difficulty = difficulty, questionText = q, optionA = a, optionB = b, optionC = c, optionD = d, correctOption = correct, explanation = explanation))
                }
                q(1, Difficulty.EASY, "Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", "B", "Mars appears red because of iron oxide on its surface.")
                q(1, Difficulty.EASY, "What gas do plants absorb from the atmosphere for photosynthesis?", "Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen", "C", "Plants use carbon dioxide and sunlight to produce glucose.")
                q(1, Difficulty.EASY, "How many bones are in an adult human body?", "206", "156", "260", "186", "A", "A typical adult skeleton has 206 bones.")
                q(1, Difficulty.EASY, "What is the boiling point of water at sea level?", "90°C", "100°C", "80°C", "120°C", "B", "At standard atmospheric pressure, water boils at 100°C.")
                q(1, Difficulty.EASY, "Which organ pumps blood through the human body?", "Lungs", "Liver", "Heart", "Kidney", "C", "The heart circulates blood through arteries and veins.")
                q(1, Difficulty.EASY, "What is the nearest star to Earth?", "Sirius", "Alpha Centauri", "The Sun", "Polaris", "C", "The Sun is Earth’s closest star.")
                q(1, Difficulty.EASY, "Which state of matter has a fixed shape and fixed volume?", "Liquid", "Gas", "Plasma", "Solid", "D", "Solids retain both their shape and volume.")
                q(1, Difficulty.EASY, "Which vitamin is mainly produced in skin by sunlight exposure?", "Vitamin A", "Vitamin C", "Vitamin D", "Vitamin K", "C", "UVB exposure helps the body synthesize Vitamin D.")
                q(1, Difficulty.EASY, "What force pulls objects toward Earth?", "Magnetism", "Gravity", "Friction", "Electricity", "B", "Gravity is the attractive force between masses.")
                q(1, Difficulty.EASY, "Which part of the plant absorbs water from soil?", "Leaf", "Stem", "Flower", "Root", "D", "Roots absorb water and minerals from the soil.")
                q(1, Difficulty.MEDIUM, "What is the pH value of pure water at 25°C?", "5", "6", "7", "8", "C", "Pure water is neutral with pH 7 at 25°C.")
                q(1, Difficulty.MEDIUM, "Which blood cells are primarily responsible for immunity?", "Red blood cells", "Platelets", "Plasma cells", "White blood cells", "D", "White blood cells defend the body against infections.")
                q(1, Difficulty.MEDIUM, "Which law states that for every action there is an equal and opposite reaction?", "Newton’s First Law", "Newton’s Second Law", "Newton’s Third Law", "Law of Gravitation", "C", "Newton’s Third Law describes paired interaction forces.")
                q(1, Difficulty.MEDIUM, "What is the chemical symbol for sodium?", "S", "So", "Na", "Sd", "C", "Na comes from the Latin word natrium.")
                q(1, Difficulty.MEDIUM, "Which layer of Earth is liquid and generates much of Earth’s magnetic field?", "Inner core", "Outer core", "Mantle", "Crust", "B", "Convection in the liquid outer core drives the geodynamo.")
                q(1, Difficulty.MEDIUM, "Which process changes a liquid into a gas at the surface below boiling point?", "Condensation", "Sublimation", "Evaporation", "Deposition", "C", "Evaporation occurs at liquid surfaces at many temperatures.")
                q(1, Difficulty.MEDIUM, "Which part of the eye controls the amount of light entering?", "Retina", "Iris", "Cornea", "Optic nerve", "B", "The iris adjusts pupil size to regulate incoming light.")
                q(1, Difficulty.MEDIUM, "What type of energy is stored in food molecules?", "Kinetic energy", "Nuclear energy", "Thermal energy", "Chemical energy", "D", "Chemical bonds in food store usable energy.")
                q(1, Difficulty.MEDIUM, "Which instrument is used to measure atmospheric pressure?", "Thermometer", "Barometer", "Hygrometer", "Anemometer", "B", "A barometer measures pressure in the atmosphere.")
                q(1, Difficulty.MEDIUM, "Which planet has the most extensive ring system?", "Mars", "Earth", "Saturn", "Mercury", "C", "Saturn’s rings are broad and highly visible.")
                q(1, Difficulty.HARD, "What is the SI unit of electric capacitance?", "Ohm", "Henry", "Farad", "Tesla", "C", "Capacitance is measured in farads (F).")
                q(1, Difficulty.HARD, "In cellular respiration, where does the Krebs cycle occur in eukaryotes?", "Cytoplasm", "Nucleus", "Ribosome", "Mitochondrial matrix", "D", "The Krebs cycle takes place in the mitochondrial matrix.")
                q(1, Difficulty.HARD, "Which particle has no electric charge and is found in the nucleus?", "Proton", "Electron", "Neutron", "Positron", "C", "Neutrons are neutral nucleons in atomic nuclei.")
                q(1, Difficulty.HARD, "What is the main greenhouse gas emitted by human activities?", "Argon", "Carbon dioxide", "Helium", "Neon", "B", "CO₂ emissions from fossil fuels are a major contributor.")
                q(1, Difficulty.HARD, "Which law relates voltage, current, and resistance in circuits?", "Boyle’s law", "Ohm’s law", "Hooke’s law", "Pascal’s law", "B", "Ohm’s law states V = I × R.")
                q(1, Difficulty.HARD, "What is the approximate speed of light in vacuum?", "3 × 10^6 m/s", "3 × 10^8 m/s", "3 × 10^5 km/s", "3 × 10^9 m/s", "B", "The accepted value is about 299,792,458 m/s.")
                q(1, Difficulty.HARD, "Which wave has the shortest wavelength in the electromagnetic spectrum?", "Radio wave", "Microwave", "X-ray", "Gamma ray", "D", "Gamma rays have the highest frequency and shortest wavelength.")
                q(1, Difficulty.HARD, "What is the process of cell division producing gametes called?", "Mitosis", "Binary fission", "Meiosis", "Budding", "C", "Meiosis halves chromosome number to form gametes.")
                q(1, Difficulty.HARD, "Which law of thermodynamics introduces entropy in isolated systems?", "Zeroth law", "First law", "Second law", "Third law", "C", "The second law states entropy tends to increase.")
                q(1, Difficulty.HARD, "What is the most abundant gas in Earth’s atmosphere?", "Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen", "C", "Nitrogen makes up about 78% of the atmosphere.")
                q(2, Difficulty.EASY, "What is 15 + 27?", "32", "42", "52", "39", "B", "15 + 27 equals 42.")
                q(2, Difficulty.EASY, "What is 9 × 8?", "72", "63", "81", "64", "A", "9 multiplied by 8 equals 72.")
                q(2, Difficulty.EASY, "What is the square root of 64?", "6", "8", "7", "9", "B", "8 × 8 = 64.")
                q(2, Difficulty.EASY, "How many degrees are in a right angle?", "45", "90", "120", "180", "B", "A right angle measures exactly 90°.")
                q(2, Difficulty.EASY, "What is 3/4 as a decimal?", "0.34", "0.70", "0.75", "0.80", "C", "3 ÷ 4 = 0.75.")
                q(2, Difficulty.EASY, "What is the perimeter of a square with side 5 cm?", "10 cm", "15 cm", "20 cm", "25 cm", "C", "Perimeter of square = 4 × side.")
                q(2, Difficulty.EASY, "What is 100 ÷ 4?", "20", "25", "40", "10", "B", "100 divided by 4 equals 25.")
                q(2, Difficulty.EASY, "Which number is prime?", "21", "39", "29", "51", "C", "29 has only two factors: 1 and 29.")
                q(2, Difficulty.EASY, "What is 12% of 50?", "5", "6", "7", "8", "B", "0.12 × 50 = 6.")
                q(2, Difficulty.EASY, "What is the value of π approximately?", "2.14", "3.14", "4.13", "3.41", "B", "Pi is approximately 3.14159.")
                q(2, Difficulty.MEDIUM, "Solve: 2x + 5 = 17. x = ?", "4", "5", "6", "7", "C", "2x = 12, so x = 6.")
                q(2, Difficulty.MEDIUM, "What is the area of a triangle with base 10 and height 6?", "60", "30", "16", "40", "B", "Area = 1/2 × base × height = 30.")
                q(2, Difficulty.MEDIUM, "What is the LCM of 6 and 8?", "12", "24", "18", "48", "B", "Least common multiple of 6 and 8 is 24.")
                q(2, Difficulty.MEDIUM, "If angles in a triangle are 50° and 60°, the third angle is:", "70°", "80°", "90°", "60°", "A", "Sum of triangle angles is 180°.")
                q(2, Difficulty.MEDIUM, "Simplify: (3^2) × (3^3)", "3^5", "3^6", "9^5", "6^3", "A", "For same base, add exponents: 2 + 3.")
                q(2, Difficulty.MEDIUM, "What is the median of 3, 7, 9, 12, 15?", "7", "9", "10", "12", "B", "Middle value in sorted odd list is 9.")
                q(2, Difficulty.MEDIUM, "What is the slope of line y = 4x - 2?", "-2", "2", "4", "0", "C", "Slope-intercept form y = mx + c has slope m = 4.")
                q(2, Difficulty.MEDIUM, "Convert 0.125 to fraction in simplest form.", "1/4", "1/8", "2/16", "3/24", "B", "0.125 = 125/1000 = 1/8.")
                q(2, Difficulty.MEDIUM, "If a circle radius is 7, diameter is:", "7", "12", "14", "21", "C", "Diameter = 2 × radius.")
                q(2, Difficulty.MEDIUM, "What is 5! (five factorial)?", "15", "60", "120", "24", "C", "5! = 5×4×3×2×1 = 120.")
                q(2, Difficulty.HARD, "Solve: x^2 - 9 = 0", "x = 3 only", "x = -3 only", "x = ±3", "No real solution", "C", "x^2 = 9 gives both +3 and -3.")
                q(2, Difficulty.HARD, "What is the derivative of x^2?", "x", "2x", "x^2", "2", "B", "Power rule: d/dx(x^n)=n*x^(n-1).")
                q(2, Difficulty.HARD, "If sin θ = 1, θ can be:", "0°", "90°", "180°", "270°", "B", "sin 90° = 1.")
                q(2, Difficulty.HARD, "Determinant of [[1,2],[3,4]] is:", "-2", "2", "10", "-10", "A", "ad - bc = 1*4 - 2*3 = -2.")
                q(2, Difficulty.HARD, "What is log10(1000)?", "2", "3", "10", "1", "B", "10^3 = 1000.")
                q(2, Difficulty.HARD, "If f(x)=2x+1, f(5)=?", "10", "11", "12", "9", "B", "f(5)=2*5+1=11.")
                q(2, Difficulty.HARD, "A sequence 2,4,8,16 is:", "Arithmetic", "Geometric", "Harmonic", "Fibonacci", "B", "Each term is multiplied by 2.")
                q(2, Difficulty.HARD, "What is the probability of getting heads in a fair coin toss?", "0", "1", "1/2", "1/3", "C", "A fair coin has equal chance for two outcomes.")
                q(2, Difficulty.HARD, "Integral of 1 dx from 0 to 5 equals:", "1", "0", "10", "5", "D", "Area under y=1 from 0 to 5 is 5.")
                q(2, Difficulty.HARD, "What is the sum of first 10 natural numbers?", "45", "55", "65", "50", "B", "n(n+1)/2 = 10*11/2 = 55.")
                q(3, Difficulty.EASY, "Which civilization built the pyramids of Giza?", "Romans", "Greeks", "Egyptians", "Mayans", "C", "The ancient Egyptians built the Giza pyramids.")
                q(3, Difficulty.EASY, "Who was the first President of the United States?", "Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams", "B", "George Washington served as the first U.S. President.")
                q(3, Difficulty.EASY, "In which year did World War II end?", "1942", "1945", "1939", "1950", "B", "WWII ended in 1945.")
                q(3, Difficulty.EASY, "The Great Wall is located in which country?", "India", "Japan", "China", "Mongolia", "C", "The Great Wall stretches across northern China.")
                q(3, Difficulty.EASY, "Who discovered sea route to India via Cape of Good Hope in 1498?", "Christopher Columbus", "Vasco da Gama", "Ferdinand Magellan", "Marco Polo", "B", "Vasco da Gama reached Calicut in 1498.")
                q(3, Difficulty.EASY, "The Renaissance began in which country?", "France", "Germany", "Italy", "England", "C", "The Renaissance started in Italian city-states.")
                q(3, Difficulty.EASY, "Which empire was ruled by Julius Caesar?", "Ottoman Empire", "Roman Republic", "Byzantine Empire", "Mongol Empire", "B", "Julius Caesar was a Roman leader in the late Republic.")
                q(3, Difficulty.EASY, "Who was known as the Maid of Orléans?", "Cleopatra", "Joan of Arc", "Queen Victoria", "Catherine the Great", "B", "Joan of Arc led French forces during the Hundred Years’ War.")
                q(3, Difficulty.EASY, "Which war was fought between North and South regions in the U.S.?", "WWI", "Civil War", "Korean War", "Vietnam War", "B", "The American Civil War was fought 1861–1865.")
                q(3, Difficulty.EASY, "What was the ship on which Pilgrims traveled to America in 1620?", "Santa Maria", "Mayflower", "Beagle", "Endeavour", "B", "Pilgrims sailed on the Mayflower.")
                q(3, Difficulty.MEDIUM, "Who wrote the Communist Manifesto with Friedrich Engels?", "Lenin", "Stalin", "Karl Marx", "Trotsky", "C", "Karl Marx co-authored the Communist Manifesto.")
                q(3, Difficulty.MEDIUM, "The fall of the Berlin Wall occurred in:", "1985", "1989", "1991", "1979", "B", "The Berlin Wall fell in November 1989.")
                q(3, Difficulty.MEDIUM, "Which treaty ended World War I?", "Treaty of Paris", "Treaty of Versailles", "Treaty of Utrecht", "Treaty of Vienna", "B", "WWI formally ended with the Treaty of Versailles.")
                q(3, Difficulty.MEDIUM, "Who was the first Emperor of unified China?", "Kublai Khan", "Qin Shi Huang", "Sun Yat-sen", "Han Wudi", "B", "Qin Shi Huang unified China in 221 BCE.")
                q(3, Difficulty.MEDIUM, "The Industrial Revolution began in:", "Russia", "United States", "Britain", "Spain", "C", "Britain pioneered industrialization in the 18th century.")
                q(3, Difficulty.MEDIUM, "Which ancient city is associated with Hammurabi’s code?", "Athens", "Babylon", "Sparta", "Rome", "B", "Hammurabi was king of Babylon.")
                q(3, Difficulty.MEDIUM, "Who led the Salt March in India in 1930?", "Jawaharlal Nehru", "Subhas Chandra Bose", "Mahatma Gandhi", "Sardar Patel", "C", "Gandhi led the civil disobedience march to Dandi.")
                q(3, Difficulty.MEDIUM, "The Ottoman Empire captured Constantinople in:", "1453", "1492", "1526", "1415", "A", "Mehmed II captured Constantinople in 1453.")
                q(3, Difficulty.MEDIUM, "Which event started in 1789 in France?", "Glorious Revolution", "French Revolution", "Reformation", "Paris Commune", "B", "The French Revolution began in 1789.")
                q(3, Difficulty.MEDIUM, "Who was the Soviet leader during much of WWII?", "Lenin", "Stalin", "Khrushchev", "Brezhnev", "B", "Joseph Stalin led the USSR during WWII.")
                q(3, Difficulty.HARD, "The Congress of Vienna was held after defeat of:", "Hitler", "Napoleon", "Kaiser Wilhelm II", "Mussolini", "B", "It reorganized Europe after Napoleon’s defeat.")
                q(3, Difficulty.HARD, "Which dynasty built most of the Forbidden City in Beijing?", "Qing", "Ming", "Han", "Tang", "B", "Construction mainly occurred during the Ming dynasty.")
                q(3, Difficulty.HARD, "The Meiji Restoration began in which year?", "1868", "1848", "1905", "1911", "A", "Meiji Restoration started in 1868.")
                q(3, Difficulty.HARD, "Who authored “The Prince,” a key Renaissance political work?", "Erasmus", "Machiavelli", "Voltaire", "Locke", "B", "Niccolò Machiavelli wrote The Prince.")
                q(3, Difficulty.HARD, "What was the immediate trigger of World War I?", "Invasion of Poland", "Sinking of Lusitania", "Assassination of Archduke Franz Ferdinand", "Russian Revolution", "C", "The Sarajevo assassination in 1914 sparked WWI.")
                q(3, Difficulty.HARD, "Which civilization developed cuneiform writing?", "Egyptians", "Sumerians", "Phoenicians", "Persians", "B", "Cuneiform originated in ancient Sumer.")
                q(3, Difficulty.HARD, "The Tokugawa shogunate ruled Japan primarily during:", "1185–1333", "1603–1868", "1868–1912", "1336–1573", "B", "Tokugawa period lasted from 1603 to 1868.")
                q(3, Difficulty.HARD, "Who was the first female Prime Minister of the UK?", "Margaret Thatcher", "Theresa May", "Indira Gandhi", "Golda Meir", "A", "Margaret Thatcher took office in 1979.")
                q(3, Difficulty.HARD, "Which agreement created the European Economic Community in 1957?", "Treaty of Lisbon", "Treaty of Rome", "Maastricht Treaty", "Schengen Agreement", "B", "The Treaty of Rome established the EEC.")
                q(3, Difficulty.HARD, "The ancient Silk Road primarily connected China with:", "Australia", "Europe and the Middle East", "Africa only", "North America", "B", "Silk Road routes linked East Asia to western markets.")
                q(4, Difficulty.EASY, "Choose the correct sentence.", "She go to school daily.", "She goes to school daily.", "She going school daily.", "She gone to school daily.", "B", "Third-person singular present takes “-es” in goes.")
                q(4, Difficulty.EASY, "What is the past tense of “eat”?", "eated", "ate", "eaten", "eating", "B", "The simple past form is “ate.”")
                q(4, Difficulty.EASY, "Which word is a noun?", "quickly", "happiness", "run", "blue", "B", "Happiness is an abstract noun.")
                q(4, Difficulty.EASY, "Identify the pronoun: “Ravi said he was late.”", "Ravi", "said", "he", "late", "C", "“He” replaces Ravi, so it is a pronoun.")
                q(4, Difficulty.EASY, "Select the correct article: “___ apple a day keeps the doctor away.”", "A", "An", "The", "No article", "B", "Use “an” before vowel sounds.")
                q(4, Difficulty.EASY, "Which punctuation ends a direct question?", "Comma", "Period", "Question mark", "Colon", "C", "Questions end with a question mark.")
                q(4, Difficulty.EASY, "Choose the adjective in: “The small cat slept.”", "small", "cat", "slept", "the", "A", "“Small” describes the noun cat.")
                q(4, Difficulty.EASY, "Which is a conjunction?", "and", "very", "quick", "under", "A", "“And” joins words or clauses.")
                q(4, Difficulty.EASY, "Opposite of “increase” is:", "raise", "grow", "expand", "decrease", "D", "Decrease is the antonym of increase.")
                q(4, Difficulty.EASY, "Choose correct plural: “child”", "childs", "children", "childes", "childer", "B", "The irregular plural of child is children.")
                q(4, Difficulty.MEDIUM, "Identify tense: “They have finished the task.”", "Simple past", "Present perfect", "Past perfect", "Future perfect", "B", "“Have finished” is present perfect tense.")
                q(4, Difficulty.MEDIUM, "Choose correct form: “If I ___ rich, I would travel.”", "am", "was", "were", "be", "C", "Subjunctive mood uses “were” with if clauses.")
                q(4, Difficulty.MEDIUM, "Pick the passive voice sentence.", "The chef cooked dinner.", "Dinner was cooked by the chef.", "The chef is cooking dinner.", "The chef cooks dinner daily.", "B", "Object-focused structure with “was cooked” is passive.")
                q(4, Difficulty.MEDIUM, "Which sentence has correct subject-verb agreement?", "The list of items are long.", "The list of items is long.", "The lists of item is long.", "The list are long.", "B", "Subject “list” is singular, so verb is “is.”")
                q(4, Difficulty.MEDIUM, "Identify adverb in: “She sings beautifully.”", "She", "sings", "beautifully", "none", "C", "“Beautifully” modifies the verb sings.")
                q(4, Difficulty.MEDIUM, "Choose the correct reported speech: He said, “I am tired.”", "He said he is tired.", "He said he was tired.", "He says he was tired.", "He said I am tired.", "B", "Backshift from am to was in reported speech.")
                q(4, Difficulty.MEDIUM, "Pick the correct sentence.", "Neither of the boys are here.", "Neither of the boys is here.", "Neither boys is here.", "Neither boy are here.", "B", "“Neither” is singular in formal agreement.")
                q(4, Difficulty.MEDIUM, "What is the comparative form of “good”?", "gooder", "more good", "better", "best", "C", "Irregular comparative of good is better.")
                q(4, Difficulty.MEDIUM, "Choose correct preposition: “She is interested ___ music.”", "on", "in", "at", "for", "B", "“Interested in” is the correct collocation.")
                q(4, Difficulty.MEDIUM, "Which sentence is an imperative?", "I enjoy reading.", "Do you read daily?", "Please close the door.", "She reads novels.", "C", "Imperatives give commands or requests.")
                q(4, Difficulty.HARD, "Identify the clause type: “Because it rained, we stayed inside.”", "Independent clause", "Noun clause", "Adverbial clause", "Relative clause", "C", "“Because it rained” modifies reason, so adverbial clause.")
                q(4, Difficulty.HARD, "Choose the sentence with correct comma usage.", "After dinner we watched a movie.", "After dinner, we watched a movie.", "After, dinner we watched a movie.", "After dinner we, watched a movie.", "B", "Introductory phrase should be followed by a comma.")
                q(4, Difficulty.HARD, "Pick the correct conditional: “If she had studied, she ___ the exam.”", "passes", "would pass", "would have passed", "will pass", "C", "Third conditional uses would have + past participle.")
                q(4, Difficulty.HARD, "Which sentence uses a semicolon correctly?", "I was late; because the bus broke down.", "I was late; the bus broke down.", "I was late; and the bus broke down.", "I was; late because the bus broke down.", "B", "Semicolon joins two closely related independent clauses.")
                q(4, Difficulty.HARD, "Identify the figure of speech: “Time is a thief.”", "Simile", "Metaphor", "Personification", "Hyperbole", "B", "It directly equates time with a thief.")
                q(4, Difficulty.HARD, "Select grammatically correct sentence.", "Each of the players have a jersey.", "Each of the players has a jersey.", "Each of players has a jersey.", "Each players has a jersey.", "B", "“Each” is singular, requiring “has.”")
                q(4, Difficulty.HARD, "Choose correct relative pronoun: “The book ___ you gave me is excellent.”", "who", "whom", "which", "whose", "C", "“Which” refers to things in restrictive clauses.")
                q(4, Difficulty.HARD, "What is the superlative form of “far” (distance)?", "farrer", "farther", "farthest", "more far", "C", "Standard superlative is farthest.")
                q(4, Difficulty.HARD, "Which sentence is in past perfect continuous tense?", "She had worked there for years before moving.", "She has been working there.", "She worked there for years.", "She had worked there yesterday.", "A", "“Had worked … for years” indicates prior ongoing past action.")
                q(4, Difficulty.HARD, "Choose the correct transformation to indirect speech: “Do you like tea?” he asked me.", "He asked me do I like tea.", "He asked me if I liked tea.", "He asked if do I like tea.", "He asked me that I liked tea.", "B", "Yes/no questions become if/whether + statement order.")
                q(5, Difficulty.EASY, "What does CPU stand for?", "Central Program Unit", "Central Processing Unit", "Computer Primary Unit", "Control Processing Unit", "B", "CPU stands for Central Processing Unit.")
                q(5, Difficulty.EASY, "Which device is primarily used for long-term data storage?", "RAM", "Cache", "Hard drive/SSD", "CPU register", "C", "Hard drives and SSDs store data persistently.")
                q(5, Difficulty.EASY, "What does HTTP stand for?", "HyperText Transfer Protocol", "HighText Transfer Package", "Hyper Transfer Text Process", "Host Transfer Text Protocol", "A", "HTTP is the protocol used for web communication.")
                q(5, Difficulty.EASY, "Which number system is used by computers internally?", "Decimal", "Binary", "Octal", "Roman", "B", "Computers process data in binary (0s and 1s).")
                q(5, Difficulty.EASY, "What is an example of an input device?", "Monitor", "Printer", "Keyboard", "Speaker", "C", "A keyboard sends input to the computer.")
                q(5, Difficulty.EASY, "What does RAM do?", "Stores files permanently", "Executes network routing", "Provides temporary working memory", "Controls screen brightness", "C", "RAM holds active data while programs run.")
                q(5, Difficulty.EASY, "Which one is an operating system?", "Chrome", "Windows", "Intel", "Python", "B", "Windows is an operating system.")
                q(5, Difficulty.EASY, "What is a bug in software?", "A security guard", "A hardware cable", "An error or flaw in code", "A type of antivirus", "C", "A bug is an unintended defect in software.")
                q(5, Difficulty.EASY, "Which key combination usually copies selected text?", "Ctrl+X", "Ctrl+C", "Ctrl+V", "Ctrl+Z", "B", "Ctrl+C copies content to clipboard.")
                q(5, Difficulty.EASY, "What does URL stand for?", "Uniform Resource Locator", "Universal Resource Link", "Unified Routing Locator", "User Resource List", "A", "URL is the address of a web resource.")
                q(5, Difficulty.MEDIUM, "Which data structure follows First-In, First-Out order?", "Stack", "Queue", "Tree", "Graph", "B", "Queues process items in FIFO order.")
                q(5, Difficulty.MEDIUM, "In object-oriented programming, what is inheritance?", "Hiding data", "Creating objects from classes", "Acquiring properties of another class", "Converting code to binary", "C", "Inheritance allows class reuse and extension.")
                q(5, Difficulty.MEDIUM, "What does SQL primarily do?", "Styles web pages", "Queries relational databases", "Compiles Java code", "Encrypts files", "B", "SQL is used to define and query relational data.")
                q(5, Difficulty.MEDIUM, "Which protocol securely encrypts web traffic?", "FTP", "HTTP", "SMTP", "HTTPS", "D", "HTTPS uses TLS for encrypted communication.")
                q(5, Difficulty.MEDIUM, "What is the time complexity of binary search on sorted data?", "O(n)", "O(log n)", "O(n log n)", "O(1)", "B", "Binary search halves the search space repeatedly.")
                q(5, Difficulty.MEDIUM, "Which Android component handles background work deferrably and reliably?", "Activity", "BroadcastReceiver", "WorkManager", "Fragment", "C", "WorkManager is suited for guaranteed deferrable tasks.")
                q(5, Difficulty.MEDIUM, "What is Git mainly used for?", "Image editing", "Version control", "Database tuning", "Video streaming", "B", "Git tracks source code changes over time.")
                q(5, Difficulty.MEDIUM, "Which one is a NoSQL database?", "PostgreSQL", "SQLite", "MongoDB", "MySQL", "C", "MongoDB is a document-oriented NoSQL database.")
                q(5, Difficulty.MEDIUM, "What is an API?", "A hardware connector", "A user interface style", "A contract for software communication", "A type of malware", "C", "APIs define how software components interact.")
                q(5, Difficulty.MEDIUM, "Which memory is typically fastest?", "HDD", "RAM", "CPU Cache", "SSD", "C", "CPU cache has very low latency.")
                q(5, Difficulty.HARD, "Which sorting algorithm has average complexity O(n log n)?", "Bubble sort", "Insertion sort", "Merge sort", "Selection sort", "C", "Merge sort runs in O(n log n) on average and worst case.")
                q(5, Difficulty.HARD, "What does ACID in databases include?", "Atomicity, Consistency, Isolation, Durability", "Availability, Consistency, Integrity, Distribution", "Atomicity, Concurrency, Isolation, Duplication", "Accuracy, Consistency, Isolation, Dependency", "A", "ACID defines reliable transaction properties.")
                q(5, Difficulty.HARD, "In networking, what is DNS mainly used for?", "Encrypting traffic", "Resolving domain names to IP addresses", "Compressing packets", "Assigning MAC addresses", "B", "DNS translates hostnames into IP addresses.")
                q(5, Difficulty.HARD, "What is a deadlock in concurrent programming?", "A memory leak", "A compile-time warning", "Processes waiting indefinitely for resources", "A fast termination", "C", "Deadlock occurs when threads block each other permanently.")
                q(5, Difficulty.HARD, "Which normal form removes transitive dependency in database design?", "1NF", "2NF", "3NF", "BCNF", "C", "3NF eliminates transitive dependencies on non-key attributes.")
                q(5, Difficulty.HARD, "What is polymorphism in OOP?", "Encrypting class data", "One interface, multiple implementations", "Multiple inheritance only", "Using only static methods", "B", "Polymorphism enables different behaviors through a common interface.")
                q(5, Difficulty.HARD, "What is Big-O notation used for?", "Measuring storage size in MB", "Describing algorithm growth rate", "Representing binary numbers", "Defining API versions", "B", "Big-O expresses asymptotic complexity.")
                q(5, Difficulty.HARD, "Which attack injects malicious SQL via user input?", "DDoS", "Phishing", "SQL Injection", "CSRF", "C", "Unsanitized inputs can alter SQL queries.")
                q(5, Difficulty.HARD, "In Kotlin coroutines, which builder launches a coroutine without blocking current thread?", "runBlocking", "launch", "sequence", "suspendMain", "B", "launch starts a new coroutine and returns immediately.")
                q(5, Difficulty.HARD, "What does hashing mainly provide in password storage?", "Reversible encryption", "One-way transformation for verification", "Lossless compression", "Network authentication", "B", "Secure hashes allow verification without storing plaintext.")
                database.questionDao().insertAll(questions)
            }
        }
    }
}
