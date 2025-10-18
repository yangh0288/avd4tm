import 'package:flutter/material.dart';

class MarketScreen extends StatefulWidget {
  const MarketScreen({super.key});
  @override
  State<MarketScreen> createState() {
    return _MarketScreenState();
  }

}

class _MarketScreenState extends State<MarketScreen>{
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text("포인트상점")
          ],
        ),
      ),
    );
  }
  
}