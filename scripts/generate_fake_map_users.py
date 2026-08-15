"""Generates SQL to seed fake users (with map locations) for local testing.

Usage:
    python scripts/generate_fake_map_users.py > scripts/fake_map_users.sql

The output SQL can be piped straight into psql, e.g.:
    docker exec -i spark-postgres psql -U postgres -d spark_db < scripts/fake_map_users.sql

Passwords for every generated user are the plaintext string below, hashed
with BCrypt (matching Spring Security's BCryptPasswordEncoder).
"""

import math
import random

import bcrypt

PLAINTEXT_PASSWORD = "Password123!"
CENTER_LAT = 43.6532  # Toronto, matches MapConfig.fallbackLatitude
CENTER_LNG = -79.3832  # matches MapConfig.fallbackLongitude
RADIUS_DEGREES = 0.09  # roughly an 8-10km scatter radius

FIRST_NAMES = [
    "Olivia", "Liam", "Emma", "Noah", "Ava", "Ethan", "Sophia", "Mason",
    "Isabella", "Lucas", "Mia", "Aiden", "Amelia", "Jackson", "Harper",
    "Logan", "Evelyn", "Elijah", "Abigail", "James", "Ella", "Benjamin",
    "Scarlett", "Henry", "Grace", "Sebastian", "Chloe", "Jack", "Zoey",
    "Owen",
]

LAST_NAMES = [
    "Nguyen", "Patel", "Kim", "Garcia", "Smith", "Singh", "Brown", "Wilson",
    "Lee", "Chen", "Martin", "Clark", "Rodriguez", "Walker", "Young",
    "Allen", "King", "Wright", "Scott", "Torres", "Nasser", "Ibrahim",
    "Kowalski", "Dubois", "Rossi", "Andersson", "Ivanov", "Suzuki",
    "Okafor", "Mensah",
]

ABOUT_TEMPLATES = [
    "Coffee enthusiast and weekend hiker exploring the city one trail at a time.",
    "Software developer by day, board game nerd by night. Always up for trivia.",
    "Foodie who loves trying every new restaurant patio in town.",
    "Yoga instructor looking to meet new people and swap book recommendations.",
    "Into live music, indie films, and long walks along the waterfront.",
    "Grad student studying design, powered by iced coffee and good playlists.",
    "Fitness junkie who never skips leg day. Dog dad to a very good boy.",
    "Traveling as much as possible between contracts. Ask me about Portugal.",
    "Amateur photographer chasing golden-hour shots of the skyline.",
    "Startup founder who still makes time for Sunday farmers markets.",
    "Bookworm, plant parent, and part-time baker of questionable sourdough.",
    "Cyclist who knows every bike path in the city. Let's ride sometime.",
    "Recently moved here and looking to make new friends and explore.",
    "Big into rock climbing and trying to convince everyone to try it too.",
    "Teacher who spends weekends volunteering and painting badly.",
    "Data analyst who loves spreadsheets almost as much as spontaneous road trips.",
    "Musician moonlighting as a barista. Will trade playlists for coffee.",
    "Runner training for my first marathon, send snack recommendations.",
    "Chef who cooks way too much food for one person, come help me eat it.",
    "Film buff with strong opinions about the best pizza place in the city.",
]

GENDERS = ["MEN", "WOMEN"]
LOOKING_FOR = ["MEN", "WOMEN", "ANY", None]


def random_offset():
    """Roughly uniform random point within a circle of RADIUS_DEGREES."""
    angle = random.uniform(0, 2 * math.pi)
    distance = RADIUS_DEGREES * math.sqrt(random.random())
    return distance * math.cos(angle), distance * math.sin(angle)


def sql_literal(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    if isinstance(value, (int, float)):
        return str(value)
    return "'" + str(value).replace("'", "''") + "'"


def main():
    random.seed(42)
    password_hash = bcrypt.hashpw(PLAINTEXT_PASSWORD.encode(), bcrypt.gensalt()).decode()

    used_usernames = set()
    rows = []
    count = 24
    for i in range(count):
        first = random.choice(FIRST_NAMES)
        last = random.choice(LAST_NAMES)
        display_name = f"{first} {last}"

        base_username = f"{first.lower()}{last.lower()}"
        username = base_username
        suffix = 1
        while username in used_usernames:
            suffix += 1
            username = f"{base_username}{suffix}"
        used_usernames.add(username)

        email = f"{username}.fake{i}@spark.test"
        age = random.randint(21, 45)
        gender = random.choice(GENDERS)
        date_looking_for = random.choice(LOOKING_FOR)
        friends_looking_for = random.choice(LOOKING_FOR)
        networking = random.choice([0, 0, 1])
        professional_development = random.choice([0, 0, 1])
        about = random.choice(ABOUT_TEMPLATES)

        dx, dy = random_offset()
        latitude = round(CENTER_LAT + dx, 6)
        longitude = round(CENTER_LNG + dy, 6)

        rows.append(
            {
                "username": username,
                "email": email,
                "password": password_hash,
                "display_name": display_name,
                "age": age,
                "about": about,
                "gender": gender,
                "date_looking_for": date_looking_for,
                "friends_looking_for": friends_looking_for,
                "networking": networking,
                "professional_development": professional_development,
                "latitude": latitude,
                "longitude": longitude,
            }
        )

    print("-- Auto-generated fake map users for local testing. Safe to re-run;")
    print("-- existing rows with the same username/email are skipped.")
    print("BEGIN;")
    for row in rows:
        columns = [
            "id", "username", "email", "password", "role",
            "display_name", "age", "about", "gender",
            "date_looking_for", "friends_looking_for",
            "networking", "professional_development",
            "latitude", "longitude", "location_updated_at",
            "created_at", "updated_at",
        ]
        values = [
            "gen_random_uuid()",
            sql_literal(row["username"]),
            sql_literal(row["email"]),
            sql_literal(row["password"]),
            "'USER'",
            sql_literal(row["display_name"]),
            sql_literal(row["age"]),
            sql_literal(row["about"]),
            sql_literal(row["gender"]),
            sql_literal(row["date_looking_for"]),
            sql_literal(row["friends_looking_for"]),
            sql_literal(row["networking"]),
            sql_literal(row["professional_development"]),
            sql_literal(row["latitude"]),
            sql_literal(row["longitude"]),
            "now()",
            "now()",
            "now()",
        ]
        print(
            f"INSERT INTO users ({', '.join(columns)}) VALUES ({', '.join(values)}) "
            "ON CONFLICT (username) DO NOTHING;"
        )
    print("COMMIT;")
    print(f"-- Plaintext password for all generated users: {PLAINTEXT_PASSWORD}")


if __name__ == "__main__":
    main()
