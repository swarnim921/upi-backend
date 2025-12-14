$baseUrl = "http://16.112.4.208:8080/api"
$rnd = Get-Random
$email = "testuser_$rnd@example.com"
$upi = "testuser_$rnd@upi"
$password = "password123"

Write-Host "---------------------------------------------------"
Write-Host "🚀 Starting E2E API Test against $baseUrl"
Write-Host "---------------------------------------------------"

Write-Host "1. Registering User: $email ..."
$regBody = @{
    name = "Test User"
    email = $email
    password = $password
    upiId = $upi
} | ConvertTo-Json
try {
    $regResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $regBody -ContentType "application/json"
    Write-Host "   ✅ Success! Registered user ID: $($regResponse.id)"
} catch {
    Write-Host "   ❌ Failed to register: $_"
    exit
}

Write-Host "`n2. Logging In ..."
$loginBody = @{
    email = $email
    password = $password
} | ConvertTo-Json
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    if ($null -eq $token) { throw "Token is null" }
    Write-Host "   ✅ Success! Got JWT Token."
} catch {
    Write-Host "   ❌ Failed to login: $_"
    exit
}

$headers = @{
    Authorization = "Bearer $token"
}

Write-Host "`n3. Getting Profile ..."
try {
    $profile = Invoke-RestMethod -Uri "$baseUrl/users/me" -Method Get -Headers $headers
    Write-Host "   ✅ Success! Logged in as: $($profile.username) | Balance: $($profile.balance)"
} catch {
    Write-Host "   ❌ Failed to get profile: $_"
}

Write-Host "`n4. Creating Payment Intent (100 INR) ..."
$intentBody = @{
    amount = 100
    currency = "INR"
    senderUpiId = $upi
    receiverUpiId = "merchant@upi"
    description = "Integration Test"
} | ConvertTo-Json

try {
    $intent = Invoke-RestMethod -Uri "$baseUrl/payments/intent" -Method Post -Body $intentBody -ContentType "application/json" -Headers $headers
    Write-Host "   ✅ Success! Created Intent ID: $($intent.intentId)"
    Write-Host "      Next Action: $($intent.nextAction)"
    
    if ($intent.nextAction -eq "upi_collect_request") {
        Write-Host "`n5. Confirming Payment (OTP: 123456) ..."
        $confirmBody = @{
            intentId = $intent.intentId
            otp = "123456"
        } | ConvertTo-Json
        $confirm = Invoke-RestMethod -Uri "$baseUrl/payments/confirm" -Method Post -Body $confirmBody -ContentType "application/json" -Headers $headers
        Write-Host "   ✅ Success! Transaction Status: $($confirm.status)"
        Write-Host "      Message: $($confirm.message)"
    }
} catch {
    Write-Host "   ❌ Failed payment flow: $_"
    Write-Host "   Details: $($_.Exception.Response | Out-String)"
}
Write-Host "---------------------------------------------------"
Write-Host "🏁 Test Complete"
