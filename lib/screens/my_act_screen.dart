import 'package:flutter/material.dart';

class MyActScreen extends StatefulWidget {
  const MyActScreen({super.key});
  @override
  State<MyActScreen> createState() {
    return _MyActScreenState();
  }

}

class _MyActScreenState extends State<MyActScreen>{
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text("활동")
          ],
        ),
      ),
    );
  }
  
}