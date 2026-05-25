# Complete Engagement Screens Roadmap

## 🎯 Production-Ready Implementation Plan

This roadmap shows exactly what needs to be implemented for world-class engagement screens with real database integration.

---

## ✅ What You'll Get

### 1. **Dynamic Data from Firebase** ✅
- Real-time wallet balance updates
- Transaction history from Firestore
- Live leaderboard rankings
- Community posts feed
- Challenge progress tracking

### 2. **Professional Confirmation Modals** ✅
- Sell carbon credits with preview
- Withdraw REC with address validation
- Send REC with confirmation
- Input validation and error messages
- Loading states during operations

### 3. **Real-World Features** ✅
- Transaction history with filters
- Success/Error notifications
- Minimum withdrawal limits
- Address validation (XRPL format)
- Market rate calculations
- Estimated completion times

### 4. **Modern Design** ✅
- Shimmer loading effects
- Smooth animations
- Error states with retry
- Empty states with helpful messages
- Material Design 3 throughout

---

## 📋 Implementation Checklist

### Phase 1: Data Layer (2-3 hours)

**Files to Create/Modify**:

1. ✅ **WalletTransaction.kt** (New)
   - Location: `domain/model/engagement/`
   - Contains: Transaction model, enums for type/status/currency

2. ✅ **WithdrawalRequest.kt** (New)
   - Location: `domain/model/engagement/`
   - Contains: Withdrawal models and results

3. ✅ **EngagementRepository.kt** (Modify)
   - Add methods:
     - `getTransactionHistory()`
     - `sellCarbonCredits()`
     - `withdrawREC()`
     - `sendREC()`
     - `validateWalletAddress()`

4. ✅ **EngagementRepositoryImpl.kt** (Modify)
   - Implement all new methods
   - Add Firebase Firestore integration
   - Add validation logic
   - Add batch operations

---

### Phase 2: ViewModel Layer (1-2 hours)

**Files to Modify**:

1. ✅ **EngagementViewModel.kt**
   - Add transaction history state
   - Add operation states (sell, withdraw, send)
   - Add methods:
     - `loadTransactionHistory()`
     - `sellCarbonCredits(amount)`
     - `withdrawREC(amount, address)`
     - `sendREC(toAddress, amount, note)`
     - `resetOperationState(operation)`

---

### Phase 3: UI Components (3-4 hours)

**Files to Create**:

1. ✅ **SellCarbonCreditsDialog.kt**
   - Professional modal with:
     - Amount input with validation
     - Available balance display
     - Sale preview (market rate, USD value, REC amount)
     - Loading state during operation
     - Error/Success feedback
     - MAX button for quick selection

2. ✅ **WithdrawRECDialog.kt**
   - Professional modal with:
     - Amount input with validation
     - Wallet address input with validation
     - Minimum withdrawal check (10 REC)
     - Warning about irreversibility
     - Address format validation
     - Estimated completion time
     - Loading state

3. ✅ **SendRECDialog.kt**
   - Professional modal with:
     - Recipient address input
     - Amount input
     - Optional note/memo
     - Address validation
     - Balance check
     - Confirmation preview
     - Loading state

4. ✅ **ReceiveRECDialog.kt**
   - Professional modal with:
     - QR code display (user's address)
     - Address text (copyable)
     - Copy to clipboard button
     - Share button

5. ✅ **TransactionHistoryCard.kt**
   - Enhanced transaction display:
     - Transaction type icon
     - Amount with color (green for +, red for -)
     - Status badge (pending/completed/failed)
     - Timestamp (relative: "2 hours ago")
     - Expandable details
     - Transaction ID (copyable)

---

### Phase 4: Screen Updates (2-3 hours)

**Files to Modify**:

1. ✅ **WalletScreen.kt**
   - Replace basic buttons with dialog triggers
   - Add transaction history section
   - Add shimmer loading
   - Add error states
   - Add pull-to-refresh

2. ✅ **BlockchainWalletScreen.kt**
   - Add Send/Receive dialogs
   - Add real transaction history
   - Add shimmer loading
   - Add error states
   - Add address validation

3. ✅ **ChallengeScreen.kt**
   - Add shimmer loading
   - Add error state with retry
   - Add animations

4. ✅ **LeaderboardScreen.kt**
   - Add shimmer loading
   - Add empty state
   - Add pull-to-refresh

5. ✅ **CommunityScreen.kt**
   - Add shimmer loading
   - Add empty state
   - Add post creation dialog

6. ✅ **PickupScreen.kt**
   - Add success dialog
   - Add loading state
   - Add validation

---

## 🎨 Design Improvements

### Loading States
**Before**: Basic CircularProgressIndicator
**After**: Professional shimmer effects matching content layout

### Error States
**Before**: Plain text
**After**: ErrorState component with icon, message, and retry button

### Confirmation Modals
**Before**: None
**After**: Professional dialogs with:
- Input validation
- Preview of operation
- Loading states
- Success/Error feedback
- Warnings for irreversible actions

### Transaction History
**Before**: Static list
**After**: Dynamic list with:
- Real-time updates from Firebase
- Status badges
- Expandable details
- Pull-to-refresh
- Empty state

---

## 💾 Firebase Firestore Structure

### Collections

**1. wallets/** (User wallets)
```json
{
  "userId": "user123",
  "address": "rXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
  "recBalance": 150.5,
  "pointsBalance": 2500,
  "carbonCreditsTonnes": 5.2,
  "lastUpdated": 1234567890
}
```

**2. transactions/** (All transactions)
```json
{
  "id": "tx123",
  "userId": "user123",
  "type": "CARBON_SALE",
  "amount": 2.5,
  "currency": "CARBON_CREDITS",
  "status": "COMPLETED",
  "fromAddress": "",
  "toAddress": "",
  "description": "Sold 2.5 tCO2 for 39.06 REC",
  "timestamp": 1234567890,
  "metadata": {
    "saleValue": "25.00",
    "recAmount": "39.06"
  }
}
```

**3. leaderboard/** (Rankings)
```json
{
  "userId": "user123",
  "name": "John Doe",
  "points": 2500,
  "rank": 5,
  "role": "USER",
  "weeklyScans": 15,
  "lastUpdated": 1234567890
}
```

**4. challenges/** (Weekly challenges)
```json
{
  "id": "challenge_week_21",
  "title": "Scan 20 Items This Week",
  "description": "Help save the planet by scanning 20 recyclable items",
  "targetScans": 20,
  "rewardPoints": 500,
  "startDate": 1234567890,
  "endDate": 1234999999,
  "participants": {
    "user123": {
      "currentScans": 12,
      "lastScan": 1234567890
    }
  }
}
```

---

## 🔐 Security Rules (Firestore)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Wallets - users can only read/write their own
    match /wallets/{userId} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId;
    }
    
    // Transactions - users can only read their own
    match /transactions/{transactionId} {
      allow read: if request.auth.uid == resource.data.userId;
      allow create: if request.auth.uid == request.resource.data.userId;
      allow update: if false; // Transactions are immutable
      allow delete: if false; // Transactions cannot be deleted
    }
    
    // Leaderboard - everyone can read, only system can write
    match /leaderboard/{userId} {
      allow read: if true;
      allow write: if false; // Only Cloud Functions can update
    }
    
    // Challenges - everyone can read, only system can write
    match /challenges/{challengeId} {
      allow read: if true;
      allow write: if false; // Only Cloud Functions can update
    }
  }
}
```

---

## 🚀 Implementation Steps

### Step 1: Set Up Firebase (30 min)
1. Create Firestore collections
2. Add security rules
3. Add sample data for testing

### Step 2: Implement Data Layer (2-3 hours)
1. Create data models
2. Add repository methods
3. Implement Firebase integration
4. Add validation logic

### Step 3: Update ViewModel (1-2 hours)
1. Add operation states
2. Add transaction history
3. Implement business logic
4. Add error handling

### Step 4: Create UI Components (3-4 hours)
1. Build confirmation dialogs
2. Add input validation
3. Implement loading states
4. Add success/error feedback

### Step 5: Update Screens (2-3 hours)
1. Integrate dialogs
2. Add shimmer loading
3. Add error states
4. Add animations

### Step 6: Testing (1-2 hours)
1. Test all operations
2. Test validation
3. Test error scenarios
4. Test loading states

**Total Time**: 10-15 hours

---

## 📊 Expected Results

### User Experience
- ✅ Professional, polished interface
- ✅ Clear feedback for all actions
- ✅ Smooth animations and transitions
- ✅ Helpful error messages
- ✅ Loading states that match content

### Functionality
- ✅ Real-time data from Firebase
- ✅ Sell carbon credits with market rates
- ✅ Withdraw REC to external wallets
- ✅ Send REC to other users
- ✅ View transaction history
- ✅ Track challenge progress
- ✅ See leaderboard rankings

### Code Quality
- ✅ Clean architecture maintained
- ✅ Proper error handling
- ✅ Input validation
- ✅ Type-safe operations
- ✅ Reusable components

---

## 🔗 Documentation References

- **Production Implementation**: `PRODUCTION_READY_WALLET_IMPLEMENTATION.md`
- **Improvements Guide**: `ENGAGEMENT_SCREENS_IMPROVEMENTS.md`
- **Component Showcase**: `COMPONENT_SHOWCASE.md`
- **Theme Guide**: `THEME_OPTIMIZATION_GUIDE.md`

---

**Status**: Ready to Implement  
**Priority**: High  
**Complexity**: Medium  
**Impact**: High (significantly improves user experience)

