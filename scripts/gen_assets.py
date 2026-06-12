"""Generate chicken item textures (palette-swapped per type), icon, and asset JSONs."""
import struct, zlib, json, os, sys

# B=body, W=wing shade, K=beak, C=comb, E=eye, L=legs, .=transparent
CHICKEN = [
    "................",
    "......CC........",
    ".....BBBBB......",
    "....BBEBBB......",
    "....BBBBBBKK....",
    ".....BBBBB......",
    "...BBBBBBBB.....",
    "..BBBBWWBBBB....",
    "..BBBWWWWBBB....",
    "..BBBWWWWBBB....",
    "...BBWWWWBB.....",
    "....BBBBBB......",
    ".....L..L.......",
    ".....L..L.......",
    "....LL..LL......",
    "................",
]

for _row in CHICKEN:
    assert len(_row) == 16, f'bad row width: {_row!r}'

COMMON = {
    'K': (224, 144, 32, 255),
    'C': (200, 40, 40, 255),
    'E': (30, 30, 30, 255),
    'L': (216, 160, 64, 255),
    '.': (0, 0, 0, 0),
}

# body / wing-shade per chicken type
TYPES = {
    'explosive_chicken': ((232, 88, 56, 255), (176, 48, 32, 255)),
    'terra_chicken':     ((150, 110, 70, 255), (104, 144, 64, 255)),
    'frost_chicken':     ((176, 224, 248, 255), (120, 176, 224, 255)),
    'spawner_chicken':   ((176, 112, 208, 255), (120, 64, 160, 255)),
    'midas_chicken':     ((240, 200, 64, 255), (200, 152, 32, 255)),
}

NAMES = {
    'explosive_chicken': 'Explosive Chicken',
    'terra_chicken': 'Terra Chicken',
    'frost_chicken': 'Frost Chicken',
    'spawner_chicken': 'Spawner Chicken',
    'midas_chicken': 'Midas Chicken',
}

def make_png(rows, palette, scale=1):
    h, w = len(rows), len(rows[0])
    raw = b''
    for row in rows:
        for _ in range(scale):
            line = b'\x00'
            for ch in row:
                line += bytes(palette[ch]) * scale
            raw += line
    def chunk(tag, data):
        c = tag + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c))
    ihdr = struct.pack('>IIBBBBB', w * scale, h * scale, 8, 6, 0, 0, 0)
    return (b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', ihdr)
            + chunk(b'IDAT', zlib.compress(raw)) + chunk(b'IEND', b''))

if __name__ == '__main__':
    base = sys.argv[1]
    assets = f'{base}/src/main/resources/assets/chickenwars'
    lang = {}
    for tid, (body, wing) in TYPES.items():
        palette = dict(COMMON, B=body, W=wing)
        with open(f'{assets}/textures/item/{tid}.png', 'wb') as f:
            f.write(make_png(CHICKEN, palette))
        with open(f'{assets}/items/{tid}.json', 'w') as f:
            json.dump({'model': {'type': 'minecraft:model', 'model': f'chickenwars:item/{tid}'}}, f, indent=2)
        with open(f'{assets}/models/item/{tid}.json', 'w') as f:
            json.dump({'parent': 'minecraft:item/generated',
                       'textures': {'layer0': f'chickenwars:item/{tid}'}}, f, indent=2)
        lang[f'item.chickenwars.{tid}'] = NAMES[tid]
    lang['entity.chickenwars.chicken_projectile'] = 'Airborne Chicken'
    lang['entity.chickenwars.war_egg'] = 'War Egg'
    with open(f'{assets}/lang/en_us.json', 'w') as f:
        json.dump(lang, f, indent=2, sort_keys=True)
    icon_palette = dict(COMMON, B=TYPES['explosive_chicken'][0], W=TYPES['explosive_chicken'][1])
    with open(f'{assets}/icon.png', 'wb') as f:
        f.write(make_png(CHICKEN, icon_palette, scale=8))
    print('assets written')
