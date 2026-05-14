Perfect — now I see your full **Recyclr** project (README + codebase). You have a solid foundation: ML scanning, gamification, blockchain (XRPL), carbon credits, community features, and a clear circular economy alignment.

Now you want me to **describe each screen with best design** (like a real production app) **and include needed database models** (Firestore schema). I’ll go screen by screen, merging what you already built with the modern UI/UX improvements we discussed earlier.

---

## 🧭 Screen Index

1. Welcome / Onboarding  
2. Home Dashboard  
3. Scan (ML + Barcode)  
4. Scan Results  
5. Rewards Store & Product Detail  
6. Token Wallet & Carbon Credits  
7. Collection Map & Pickup Request  
8. Profile & Impact Dashboard  
9. Community Feed  
10. Leaderboards & Challenges  
11. $REC Blockchain Wallet (standalone)

---

## 1. Welcome / Onboarding

### 🎨 Best Design

- **Full-screen gradient** (green to light green: `#2E7D32` → `#A5D6A7`)  
- **Central illustration**: animated recycling icon / Lake Tanganyika wave  
- **Headline**: `"Salam, let's clean Burundi together"` (22sp bold white)  
- **Bullet points** (white, 14sp, with green checkmarks):  
  - Track your impact  
  - Earn points & carbon credits  
  - Join community challenges  
- **Buttons**:  
  - `Continue with Google` (white background, green text, corner 28dp)  
  - `Continue as Guest` (transparent, white border)  
- **Footer**: `By continuing, you agree to Terms & Privacy` (10sp grey)

### 🗄️ Database Models

No data written at onboarding (only after auth).  
But you need a `users` collection created after sign‑in:

```ts
// users/{userId}
{
  uid: string,
  email: string,
  displayName: string,
  photoURL?: string,
  createdAt: timestamp,
  totalPoints: number,        // starts 0
  totalCO2Saved: number,      // kg
  treesPlanted: number,
  ecoStreak: number,
  lastScanDate: timestamp,
  isGuest: boolean,
  referredBy?: string         // userId of referrer
}
```

---

## 2. Home Dashboard

### 🎨 Best Design

*(Full spec already provided earlier – I’ll summarise key points here)*

- **Header**: Avatar + `"Welcome back, Aline"` + notification bell  
- **Stats row** (3 cards): CO₂ saved (kg), Points, Trees planted  
- **Wallet summary**: `0.78 REC` + `0.05t CO2 credits` + `View details ›`  
- **Eco‑Streak badge**: 🔥 `1 days · x1.0 multiplier`  
- **Weekly challenge card**: progress bar + description + points reward  
- **Leaderboard preview**: top 3 + `You` row highlighted  
- **Community highlight**: latest post from user’s group  
- **Categories** (horizontal scroll): Plastic, Paper, Glass, Metal, Textile  
- **Bottom navigation**: Home (selected), Scan, Map, Shop, Profile

### 🗄️ Database Models

```ts
// users/{userId}
// (already defined, plus home-specific fields)
{
  // ... previous fields
  weeklyChallengeProgress: number,    // e.g., 26/40 scans
  weeklyChallengeId: string,
  lastWeeklyReset: timestamp
}

// weekly_challenges/{challengeId}
{
  title: string,          // "Clean Lake Tanganyika Week"
  description: string,
  target: number,         // 40 scans
  pointsReward: number,   // 300
  startDate: timestamp,
  endDate: timestamp,
  active: boolean
}

// leaderboards (aggregated view – can be computed query)
// No separate doc; query users sorted by totalPoints descending, limit 10
```

---

## 3. Scan Screen (ML + Barcode)

### 🎨 Best Design

- **Camera preview** (full width, height 50% of screen)  
- **Overlay rectangle** with rounded corners indicating scan area  
- **Two buttons** below preview:  
  - `📷 Scan Item` (green filled, large) – opens ML detection  
  - `🔍 Scan Barcode` (outline) – opens barcode scanner  
- **Manual entry** link: `Enter item name` (text link)  
- **Recent scans** (horizontal list of last 3 scanned items, with icons)  
- **Flash toggle** and **Gallery pick** (top right icons)

### 🗄️ Database Models

```ts
// scans/{scanId}
{
  userId: string,
  type: "ml" | "barcode" | "manual",
  material: string,            // plastic, paper, glass, metal, textile, e-waste, hazardous
  weightKg: number,            // estimated or user input
  pointsEarned: number,
  co2SavedKg: number,
  verified: boolean,           // after photo verification
  timestamp: timestamp,
  location: geopoint,          // lat/lng
  barcode?: string,
  imageUrl?: string            // photo taken for verification
}

// barcodes/{barcode}
{
  productName: string,
  material: string,
  recyclability: "recyclable" | "non-recyclable" | "hazardous",
  pointsValue: number,
  submittedBy: string,         // userId
  verified: boolean
}
```

---

## 4. Scan Results Screen

### 🎨 Best Design

- **Success animation** (confetti for first scan of the day)  
- **Item image** (taken from camera or product placeholder)  
- **Title**: `"Plastic Bottle – Recyclable"`  
- **Stats card**:  
  - `+15 points` (green large text)  
  - `0.03 kg CO₂ saved`  
- **Disposal instructions**: `Rinse, crush, place in yellow bin`  
- **Map suggestion**: `Nearest collection point: 200m away` (clickable)  
- **Buttons**:  
  - `Done` (returns to Home)  
  - `Share impact` (opens share sheet)  
  - `Verify for bonus` (takes photo inside bin – double points)  

### 🗄️ Database Models

*(Same `scans` collection as above; after verification, update `verified: true` and add `verificationPhotoUrl`)*

---

## 5. Rewards Store & Product Detail

### 🎨 Best Design

**Store list** (grid or list):  
- Each item shows: image, title, points cost, and `Redeem` button  
- Tabs: `Shop Items` | `Donations`  
- Your balance: `78 pts` in top bar  

**Product Detail** *(full Amazon‑like spec already given – summary)*:  
- Image gallery (zoomable), title, category, rating  
- Price: `100 pts` + `Market price ~~200 pts~~` + `You save 50%`  
- Stock & verification badge  
- Description + key details grid (carbon offset, location, etc.)  
- Reviews preview  
- `Redeem Now` (sticky) + `Add to Wishlist`  

### 🗄️ Database Models

```ts
// products/{productId}
{
  title: string,
  category: string,           // "nature", "bags", "education", "home"
  pointsCost: number,
  marketPrice?: number,
  description: string,
  images: string[],           // URLs
  stock: number,              // -1 for unlimited
  isDonation: boolean,
  impactMetric: string,       // e.g., "Plants 1 tree"
  sellerId: string,           // Recyclr or partner
  createdAt: timestamp,
  reviewsCount: number,
  avgRating: number
}

// redemptions/{redemptionId}
{
  userId: string,
  productId: string,
  pointsSpent: number,
  status: "pending" | "completed" | "cancelled",
  redemptionDate: timestamp,
  deliveryDetails?: {
    address: string,
    trackingId?: string
  }
}

// reviews/{reviewId}
{
  productId: string,
  userId: string,
  rating: number,      // 1-5
  comment: string,
  timestamp: timestamp
}
```

---

## 6. Token Wallet & Carbon Credits

### 🎨 Best Design

- **Header**: `$REC Wallet` + settings icon  
- **Balance card**:  
  - `$REC tokens: 0.78`  
  - `≈ 78 points` (small)  
  - Graph: last 7 days balance (mock line chart)  
- **Carbon credits card**:  
  - `🌱 Carbon Credits Earned: 0.05 tCO₂`  
  - `Market value: $0.50` (placeholder)  
- **Two buttons**:  
  - `Sell Carbon Credits` (only for aggregated B2B) – opens info dialog  
  - `Donate $REC to a cause` – opens list of community funds  
- **Transaction history** (list):  
  - `+5 $REC from scan` (timestamp)  
  - `-2 $REC redeemed for tree`  

### 🗄️ Database Models

```ts
// token_transactions/{txId}
{
  userId: string,
  amount: number,          // positive = earned, negative = spent
  type: "scan" | "redemption" | "carbon_sale" | "donation",
  referenceId: string,     // scanId, redemptionId, etc.
  timestamp: timestamp,
  txHash?: string          // XRPL transaction hash
}

// carbon_credits/{creditId}
{
  userId: string,
  amountTonnes: number,
  verified: boolean,
  batchId?: string,        // when sold in bulk
  status: "available" | "sold",
  salePriceUsd?: number
}

// user_wallet/{userId}  (denormalised)
{
  recBalance: number,      // $REC tokens (decimal)
  carbonCreditsBalance: number  // tonnes
}
```

---

## 7. Collection Map & Pickup Request

### 🎨 Best Design

**Map screen**:  
- Full-screen Google Map with:  
  - Green markers = drop‑off points  
  - Blue marker = user’s location  
  - Orange marker = scheduled pickup point  

**Bottom sheet** (draggable):  
- `Request Pickup` button  
- Subscription toggle: `Repeat every 2 weeks` (discount badge)  
- Item selector (chips: Plastic, Paper, etc.)  
- Weight slider (0–50 kg)  
- Date/time picker  

**After request**:  
- Confirmation screen with collector ETA (from Firestore)  
- QR code for collector to scan upon arrival  

### 🗄️ Database Models

```ts
// collection_points/{pointId}
{
  name: string,
  location: geopoint,
  address: string,
  acceptedMaterials: string[],
  openingHours: string,
  contactPhone: string
}

// pickup_requests/{requestId}
{
  userId: string,
  collectorId?: string,      // assigned collector
  items: string[],           // ["plastic","paper"]
  weightKg: number,
  scheduledTime: timestamp,
  status: "pending" | "assigned" | "in_progress" | "completed" | "cancelled",
  subscription: boolean,
  pointsCost: number,
  qrCode: string,
  createdAt: timestamp,
  completedAt?: timestamp,
  rating?: number
}

// collectors/{collectorId}  (for partner app)
{
  name: string,
  phone: string,
  vehicleType: string,
  isAvailable: boolean,
  currentLocation?: geopoint
}
```

---

## 8. Profile & Impact Dashboard

### 🎨 Best Design

- **Header**: Avatar, name, email, `Edit Profile` link  
- **Stats cards** (horizontal scroll):  
  - Total points, total CO₂ saved, trees planted, items recycled  
- **Badges** (grid of earned badges):  
  - `Eco‑Streak (7 days)`  
  - `Verified Scanner`  
  - `Community Hero`  
- **Impact graph**: weekly points earned (bar chart)  
- **Settings** (gear icon) leads to full Settings screen  
- **Log out** / **Delete account** (with confirmation)  

### 🗄️ Database Models

```ts
// badges/{badgeId}
{
  name: string,
  description: string,
  iconUrl: string,
  criteria: string   // e.g., "streak >= 7"
}

// user_badges/{userId_badgeId}
{
  userId: string,
  badgeId: string,
  earnedAt: timestamp
}

// user_impact_stats/{userId}  (aggregated)
{
  totalPoints: number,
  totalCO2Kg: number,
  totalTrees: number,
  totalItemsRecycled: number,
  currentStreak: number,
  longestStreak: number
}
```

---

## 9. Community Feed

### 🎨 Best Design

- **Top bar**: `Community` + camera icon (create post)  
- **Two tabs**: `For You` (posts from your groups) | `Reports` (illegal dumping)  
- **Post card**:  
  - Avatar + name + time stamp  
  - Image (if any) + caption  
  - Like button (❤️ 36) + comment button + share  
  - If illegal dumping report: `Report status: Under review` badge  
- **Create post flow**:  
  - Bottom sheet with camera/gallery, text input, group selector, `Post` button  
  - Option to mark as `Illegal dumping` (geotag required)  

### 🗄️ Database Models

```ts
// posts/{postId}
{
  userId: string,
  groupId: string,
  content: string,
  imageUrl?: string,
  type: "normal" | "illegal_dumping",
  location?: geopoint,
  likeCount: number,
  commentCount: number,
  status: "active" | "flagged" | "resolved",
  createdAt: timestamp
}

// groups/{groupId}
{
  name: string,
  description: string,
  memberCount: number,
  coverImage: string,
  createdBy: string,
  createdAt: timestamp
}

// user_groups/{userId_groupId}
{
  userId: string,
  groupId: string,
  joinedAt: timestamp,
  role: "member" | "admin"
}

// likes/{postId_userId}
{
  postId: string,
  userId: string,
  timestamp: timestamp
}

// comments/{commentId}
{
  postId: string,
  userId: string,
  content: string,
  createdAt: timestamp
}
```

---

## 10. Leaderboards & Challenges

### 🎨 Best Design

- **Tab layout**: `Weekly` | `All Time` | `Friends`  
- **Leaderboard list**:  
  - Rank, avatar, name, points, level badge  
  - Highlight current user’s row in green  
- **Active challenge** at top:  
  - Title, description, progress bar, remaining time  
  - `View challenge details` button  
- **Your stats card**:  
  - Your points this week, your rank, points needed to reach next rank  

### 🗄️ Database Models

*(Reuses `users` for points, `weekly_challenges` as defined earlier, plus a `leaderboard_cache` document updated daily by Cloud Function)*

```ts
// leaderboard_cache_weekly
{
  weekStartDate: timestamp,
  rankings: [
    { userId: string, rank: number, points: number, displayName: string }
  ]
}
```

---

## 11. $REC Blockchain Wallet (standalone screen)

### 🎨 Best Design

- **Token balance** (large, 32sp) + **USD equivalent** (if market price available)  
- **Send / Receive buttons** (if transferable)  
- **Transaction history** (from XRPL ledger via WebSocket)  
- **Link to XUMM** (optional – “Connect XUMM for private key”)  
- **Carbon credit marketplace** tab:  
  - List of available batches (size, price per tonne)  
  - `Buy credits` (for corporations)  
- **Security notice**: seed phrase backup reminder  

### 🗄️ Database Models

*(In addition to `token_transactions` above, you need XRPL account mapping)*

```ts
// xrpl_accounts/{userId}
{
  address: string,          // r... on XRPL
  publicKey: string,
  encryptedPrivateKey?: string, // if user chooses custodial
  isCustodial: boolean
}
```

---

## ✅ Summary – What You Have vs. What’s Added

| Screen | Existing in your README | New design spec | Database models provided |
|--------|------------------------|----------------|--------------------------|
| Welcome | ✅ | ✅ full UI | ✅ |
| Home | ✅ partial | ✅ full spec | ✅ |
| Scan | ✅ ML + barcode | ✅ camera overlay + recent scans | ✅ |
| Results | ✅ points & CO₂ | ✅ verification bonus | ✅ |
| Rewards Store | ✅ points store | ✅ Amazon-like detail | ✅ |
| Token Wallet | ✅ XRPL + carbon credits | ✅ balance + graph + history | ✅ |
| Map & Pickup | ✅ on-demand | ✅ subscription + QR | ✅ |
| Profile | ✅ impact dashboard | ✅ badges + graph | ✅ |
| Community Feed | ✅ new feature | ✅ feed + reports | ✅ |
| Leaderboards | ✅ new feature | ✅ tabs + challenge | ✅ |
| Blockchain Wallet | ✅ new feature | ✅ send/receive + XUMM | ✅ |

---

## 🚀 Next Step

You now have **production‑ready UI specifications** and **Firestore data models** for every Recyclr screen.  

Would you like me to:
1. **Generate the actual XML layouts** (for any specific screen)?  
2. **Write the Firestore security rules** for all these collections?  
3. **Create the Kotlin ViewHolder classes** for RecyclerView adapters based on these models?  

Just tell me where to focus next.