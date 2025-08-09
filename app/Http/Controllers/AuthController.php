<?php

namespace App\Http\Controllers;

use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Hashing\BcryptHasher;
use Illuminate\Http\Request;
use Illuminate\Validation\Rules\Password;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    public function register(Request $request): array
    {
        $request->validate([
            'name' => 'required|max:64',
            'email' => 'required|email:rfc',
            'password' => ['required', Password::min(8)->letters()->numbers()->mixedCase()],
            'gender' => 'required|boolean:strict'
        ]);

        $user = User::create($request->all());
        $token = $user->createToken('token');
        return [
            'user' => new UserResource($user),
            'token' => $token->plainTextToken,
        ];
    }

    public function login(Request $request): array
    {
        $user = User::where('email', $request->email)->first();
        if (! $user) {
            return ['message' => 'email not found'];
        }
        if (Hash::check($request->password, $user->password)) {
            $token = $user->createToken('token');
            return [
                'user' => $user,
                'token' => $token->plainTextToken
            ];
        }

        return ['message' => 'login or password is incorrect'];
    }

    public function me(Request $request)
    {
        return response()->json($request->user());
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Logged out']);
    }
}
